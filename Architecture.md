# Software Architecture of the RINPn

&raquo; [Japanese](./Architecture_Japanese.md)

Here we will explain the internal architecture of this software, which help you to grasp the global structure of the implementation before reading source code.

<a id="architecture-abstract"></a>
## Abstract and a Block Diagram

The architecture of the RINPn adopts the MVP pattern which consists mainly of 3 core components: Model, View, and Presenter.
Each component is packed as a package.


In addition, although it is completely independent from the implementation of the RINPn, the script engine of the Vnano to take calculations is also an important component from the point of view of the architecture of the whole software.


The following is a block diagram to grasp relationship between components we mentioned above:

![Block Diagram](./img/architecture.jpg)

As in the above diagram, 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/RinearnProcessorNano.java">RinearnProcessorNano</a> 
class plays the role of the outer frame of implementation of this software, 
and in there Model/View/Presenter components are combined and work together.
In the following, we will explain roles of components.



<a id="architecture-model"></a>
## Model ( <a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/">com.rinearn.processornano.model</a> package )

This component provides the functional aspects of the calculator, excepting the UI.
For example, this component takes a calculation expression as an input, and return the calculated result as an output.


In the GUI mode, input/output (I/O) to this component are performed in event-driven ways through the Presenter, so it is a little difficult to grasp the processing flow (see <a href="#architecture-presenter">the explanation of the Presenter</a> for details).
In the contrast, the processing flow in the CUI mode is continuous and synchronized, so it is very easy to grasp: Firstly, 
"calculate" method of 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/CalculatorModel.java">CalculatorModel</a> 
class will be called from the "calculate" method of 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/RinearnProcessorNano.java">RinearnProcessorNano</a> 
class, which will be called from "main" method.
The calculation expression inputted from the command-line will be passed as an argument of the method, so then next, it takes the calculation by calling the script engine (see <a href="#architecture-engine">the explanation of the script engine</a> for details), and output the result to the standard-output.



<a id="architecture-view"></a>
## View ( <a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/view/">com.rinearn.processornano.view</a> package )

In the GUI mode, this component play the role of the graphical surface of the UI, which composed of a window, text fields, and so on.
Please note that this component does NOT handle any events from the UI by itself (it is a role of the <a href="#architecture-presenter">Presenter</a>).


The implementation of the surface of the UI is provided by 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/view/ViewImpl.java">ViewImpl</a> class, 
and an instance of this class is accessed through 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/view/ViewInterface.java">ViewInterface</a> interface 
from outside of the View component (package).
An instance of ViewImpl class is initialized by 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/view/ViewInitializer.java">ViewInitializer</a> class on the event-dispatching thread  (by using the feature of "SwingUtilities.invokeAndWait" method).



<a id="architecture-presenter"></a>
## Presenter ( <a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/">com.rinearn.processornano.presenter</a> package )

This component mediates between the Model and the View.
Classes in this component will behave individually in event-driven ways, after they are linked to the Model and the View by "link" method of 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/Presenter.java">Presenter</a> 
class.



The action to the UI by the user to perform a calculation will be catched by UI event listeners ( e.g. 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/RunKeyListener.java">RunKeyListener</a> 
class, 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/RunButtonListener.java">RunButtonListener</a> 
class, and so on
) in this component.
Then those listeners create a new thread and an instance of 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/AsynchronousCalculationRunner.java">AsynchronousCalculationRunner</a> 
class of "model" package, to run calculation asynchronously on the created thread. 
AsynchronousCalculationRunner class executes the calculation by calling "calculate" 
method of 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/CalculatorModel.java">CalculatorModel</a> 
class, and notify the result to a calculation event listener (defined in RunButton class) which imprementing 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/AsynchronousCalculationListener.java">AsynchronousCalculationListener</a> 
interface.
Finally, the calculation event listener requests to the event-dispatching thread
to update the UI on by using view updater classes ( e.g. 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/presenter/OutputFieldUpdater.java">OutputFieldUpdater</a>, and so on).








<a id="architecture-engine"></a>
## Script Engine ( <a href="https://github.com/RINEARN/vnano/blob/master/src/org/vcssl/nano/">org.vcssl.nano</a> package )

This component takes calculations requested by the Model. 
Executions of scripts, and communications with plug-ins, are also taken by this component.
By the way, this component is being developed independently as the compact script engine "Vnano" for embedded use in applications.
Therefore, for details of this component itself, see the document of: <a href="https://github.com/RINEARN/vnano">https://github.com/RINEARN/vnano</a>



This component is accessed from the Model through javax.script.ScriptEngine interface of Java&reg; Scripting API.
On the CUI mode, "eval" method of the script engine is simply called in the processing of the "calculate" method of 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/CalculatorModel.java">CalculatorModel</a> class.
The "eval" method takes a calculation expression (or script) as an argument, and returns the calculated result as a return-value.


On the GUI mode, when an calculation is requested by the user,
an instance of <a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/AsynchronousCalculationRunner.java">AsynchronousCalculationRunner</a> class will be created, and "run" method of the class will be invoked on an other thread.
From there, "calculate" method of 
<a href="https://github.com/RINEARN/rinpn/blob/master/src/com/rinearn/processornano/model/CalculatorModel.java">CalculatorModel</a>
class which we mentioned above will be called, and "eval" method of the script engine will be called in there (see also: <a href="#architecture-presenter">the explanation of the Presenter</a>).




---

## Credits

- Oracle and Java are registered trademarks of Oracle and/or its affiliates. 

- Other names may be either a registered trademarks or trademarks of their respective owners. 



