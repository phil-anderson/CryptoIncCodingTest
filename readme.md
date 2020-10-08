# Crypto Inc Coding Test
It turns out I've done a very similar test to this before, a couple of years ago. At that time I did it in Java and 
maintained a summary as each order was placed, to make reporting very fast.
 
So... In order to change things around a bit I decided to do it in Kotlin this time, and took the opposite approach of 
generating the summary each time on request. I hope that's OK.

I used gradle for the build, so you should be able to build and run it using gradlew (included)

## Design Choices
* I used BigDecimal for prices and quantities to avoid the rounding issues inherent in floats / doubles
* I didn't code defensively. The code assumes that orders passed to it are well-formed.
* According to the test paper the summary is only grouped by price, so I assumed that an order type (i.e. buy or sell)
and coin type should be specified when requesting the summary. The summary is then filtered accordngly. It makes little
sense to me to aggregate buys with sells, or Etherium orders with Litecoin ones although in a real-world situation I 
would seek clarification on this. 

Many thanks,
Phil

