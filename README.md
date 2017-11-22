# Store Application

1. Download the version 2.5.18 of the Play Framework from [http://www.playframework.com/]
2. Unzip the contents the StoreApp zip file.
3. Enter the above directory in a shell (e.g. cmd) and execute the "sbt run" command.
4. Open a web browser and navigate to localhost:9000, you should see the (extremely basic) homepage for the Store App.

## Task Purpose
For this task you will be completing some basic functionality for a retail e-commerce web site using Play Framework. 
You do not need to delve into UI and design concerns (assume someone else will worry about making things pretty). We are
looking for a solution that reacts appropriately to requests, produces the correct data in responses, is resilient and 
performs well. Provide documentation in the code for any special considerations or concerns regarding your solution. 

## Task Requirements with Status
1. Create a category page that displays:
    * DONE - The name of the category,
    * DONE - A list of the items in the category (show their name) with each item being a link to the product details page.
2. Create a product details page that displays:
    * DONE - The product name,
    * WIP - The product's price,
    * NOT STARTED - Any attributes that are defined in the product info. (Format as you like, we don’t care, we’re backend developers.)
3. SOME TESTS ("sbt test"") - Include appropriate unit tests.
