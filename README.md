# Quick-Pic-App

## Description:
QuickPic is an android app designed to streamline management of product inventory. Our app addresses the common inventory management challenges faced by people by providing a solution that allows users to easily retrieve product details and maintain information about available units. By simply capturing a picture of the product barcode and uploading it to the app, users can quickly obtain the necessary information. This innovative feature simplifies the inventory management process, making it more efficient and user-friendly.</ br>
The app utilizes powerful machine learning models from Amazon Rekognition for image recognition capabilities, enabling users to easily identify objects and labels in pictures. With its advanced barcode reading capabilities, QuickPic serves as a versatile tool for product inventory management. The app has the ability to recognize real-world objects and automatically categorize, classify, translate, and perform a range of useful operations on them without requiring any human intervention.</ br>
The app saves the product inventory on a database, providing users with quick and easy access to relevant information. In addition, the app provides the ability to search for products in the inventory by their name. Users can further organize their inventory by adding tags to products. We integrate OpenAI 'gpt-3.4' model to give us a prompt summary of the product.</ br>

## Project Requirements:-
1.	Create products and add them to the inventory.
2.	Search for products in the inventory.
3.	Categorize products by adding tags.
4.	Upload an image of the product with barcode and extract product details using image recognition capabilities leveraging machine learning models from Amazon Rekognition.
5.	Summarize the Product using OpenAI 'gpt-3.4' model.

## Technical Requirements:-
1.	Node.js backend server - Server side scripting to build REST API’s.
2.	Android application - Mobile application development using Java.
3.	MongoDB - Primary database.
4.	AWS Lambda - Host serverless backend REST API’s in the cloud.
5.	Amazon Rekognition - Image recognition using machine learning.
6.	OpenAI: Product Summarization.

## High-Level Architecture Design:
The high-level architecture design consists of:
1.	Mobile application: Developed in Java for Android devices, serves as the user interface
2.	Backend server: Built using Node.js, hosts REST APIs, and handles communication between the mobile app and other services
3.	Database: MongoDB stores product inventory and user data
4.	AWS Elastic BeanStalk: Hosts backend REST APIs in the cloud
5.	Amazon Rekognition: Provides image recognition and barcode scanning capabilities
6.	OpenAI: Offers product summarization using the 'gpt-3.4' model.

![image](https://github.com/Utsavrawar27/Quick-Pic-App/assets/40047632/0e04255e-e67a-4a5a-a18f-754fd5100652)
