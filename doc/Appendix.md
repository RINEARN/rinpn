# Appendix - List of Built-in Functions/Variables

&raquo; [Japanese](Appendix_Japanese.md)

&raquo; [Ask the AI for help (ChatGPT account required)](https://chatgpt.com/g/g-Hu225rEdv-rinpn-assistant)

RINPn comes equipped with a variety of built-in functions and variables. Below is a list of some of the most frequently used ones, provided primarily through Vnano Standard Plugins.

For a comprehensive list and detailed specifications, please refer to the specification documents of [Vnano Standard Plugins](https://www.vcssl.org/en-us/vnano/plugin/).


## Built-in Functions

### [rad( degree )](https://www.vcssl.org/en-us/vnano/plugin/math/#rad)

Converts degrees to radians.

Example: rad( 180.0 )

### [deg( radian )](https://www.vcssl.org/en-us/vnano/plugin/math/#deg)

Converts radians to degrees.

Example: deg( 2.0 * PI )

### [sin( x )](https://www.vcssl.org/en-us/vnano/plugin/math/#sin)

Calculates the sine of x, where x is in radians.

Example: sin( PI / 2.0 )

### [cos( x )](https://www.vcssl.org/en-us/vnano/plugin/math/#cos)

Calculates the cosine of x, where x is in radians.

Example: cos( 2.0 * PI )

### [tan( x )](https://www.vcssl.org/en-us/vnano/plugin/math/#tan)

Calculates the tangent of x, where x is in radians.

Example: tan( PI / 4.0 )

### [asin( x )](https://www.vcssl.org/en-us/vnano/plugin/math/#asin)

Returns the arc-sine of x in radians.

Example: asin( 1.0 )

### [acos( x )](https://www.vcssl.org/en-us/vnano/plugin/math/#acos)

Returns the arc-cosine of x in radians.

Example: acos( 1.0 )

### [atan( x )](https://www.vcssl.org/en-us/vnano/plugin/math/#atan)

Returns the arc-tangent of x in radians.

Example: atan( 1.0 )

### [sqrt( x )](https://www.vcssl.org/en-us/vnano/plugin/math/#sqrt)

Calculates the square root of x.

Example: sqrt( 4.0 )

### [ln( x )](https://www.vcssl.org/en-us/vnano/plugin/math/#ln)

Calculates the natural logarithm (base e) of x.

Example: ln( 10.0 )

### [log10( x )](https://www.vcssl.org/en-us/vnano/plugin/math/#log10)

Calculates the logarithm (base 10) of x.

Example: log10( 1000.0 )

### [pow( x, exponent )](https://www.vcssl.org/en-us/vnano/plugin/math/#pow)

Returns x raised to the power of "exponent".

Example: pow( 2.0, 3.0 )

### [exp( exponent )](https://www.vcssl.org/en-us/vnano/plugin/math/#exp)

Returns e raised to the power of "exponent". The function which returns the value of "e" (napier number) to "exponent"-power.

Example: exp( 1.2 )

### [abs( x )](https://www.vcssl.org/en-us/vnano/plugin/math/#abs)

Returns the absolute value of x.

Example: abs( -1.23 )

### [sum( ... )](https://www.vcssl.org/en-us/vnano/plugin/math/#sum)

Calculates the sum of all given arguments

Example: sum( 1.23 ,   4.56 ,   7.89 )

### [mean( ... )](https://www.vcssl.org/en-us/vnano/plugin/math/#mean)

Calculates the arithmetic mean of all given arguments.

Example: mean( 1.23 ,   4.56 ,   7.89 )

### [van( ... )](https://www.vcssl.org/en-us/vnano/plugin/math/#van)

Calculates the variance (denominator n) of given arguments.

Example: van( 1.23 ,   4.56 ,   7.89 )

### [van1( ... )](https://www.vcssl.org/en-us/vnano/plugin/math/#van1)

Calculates the variance (denominator n-1) of given arguments.

Example: van1( 1.23 ,   4.56 ,   7.89 )

### [sdn( ... )](https://www.vcssl.org/en-us/vnano/plugin/math/#sdn)

Calculates the standard deviation (denominator n) of given arguments.

Example: sdn( 1.23 ,   4.56 ,   7.89 )

### [sdn1( ... )](https://www.vcssl.org/en-us/vnano/plugin/math/#sdn1)

Calculates the standard deviation (denominator n-1) of given arguments.

Example: sdn1( 1.23 ,   4.56 ,   7.89 )

### [length( array, dim )](https://www.vcssl.org/en-us/vnano/plugin/system/#length)

Returns the length of the specified ("dim"-th) dimension of an array.

Example: length( array, 0 )

### [output( value )](https://www.vcssl.org/en-us/vnano/plugin/system/#output)

This function displays the calculated value from scripts. In GUI mode, the value is displayed in the "OUTPUT" text field and will be overwritten if the function is called multiple times. In CUI mode, the value is output to the standard output as a single line each time it is called.

Example: output( 1.23 )

### [print( value )](https://www.vcssl.org/en-us/vnano/plugin/system/#print)

This function is designed to display long texts and multiple lines for general purposes. It can display content consisting of any number of values or arrays, separated by tab spaces. In GUI mode, the content is appended to a text area in an independent window without line breaks. In CUI mode, the content is output to the standard output without line breaks.

Example: print( 1.2 , 3.4 , 5.6 )

### [println( value )](https://www.vcssl.org/en-us/vnano/plugin/system/#println)

Similar to print, but includes line feedings.

Example: println( 1.2 , 3.4 , 5.6 )

## Built-in Variables

### [PI](https://www.vcssl.org/en-us/vnano/plugin/math/#PI)

Stores the value of π.

Value: 3.141592653589793


---

## Credits and Trademarks

* ChatGPT is a trademark or a registered trademark of OpenAI OpCo, LLC in the United States and other countries.

* Other names may be either a registered trademarks or trademarks of their respective owners.



