# EnglishToHindi Translator  

## About  

Deep learning techniques has revolutionized machine translation task. And we cannot deny impact made by attention mechanism in the journey of machine translation. First attention mechanism is proposed by  Bahdanau et al in 2015, from there, it was never look-back. Now we have BERT,GPT that can produce relaible and sustainable machine translator.  
In this project, I have implemented attention mechanism proposed by Bahdanau et al. I used eng-hindi dataset owned by IITB. After training the deep learning model in tensorflow, model is converted to tflite. And then it is used to prepare a android application.  

#### Architecture:  

![image](https://user-images.githubusercontent.com/39105103/121017629-1defd200-c7bb-11eb-906d-48e9e816ee01.png)
  
  
## How to use it  

**a. Directly install apk file from 'App/EngHin_Translator.apk' on any andeoid device.**



**b. Use this framework to build your own translator. To do so, following are the steps to follow-->**

1. Go to --> 'ML model/English_Hindi_Translator_final.ipynb' tweak some parameters or use different model. Running all the cells will create :  
   - TF model
   - .Tflite model
   - Word to index dictionary for both hindi and Endglish in json format
Note: Change the path whenever required.  

2. Copy .Tflite model and json files created in step 1 and paste it in --> 'Source code\app\src\main\assets'  
3. Run the source code in android studio. Thats it!
 
