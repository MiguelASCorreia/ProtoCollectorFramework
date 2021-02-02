# ProtoCollectorFramework

This framework was developed for the purpose of favoring the creation of android mobile applications that target the issue of phytosanitary management where the user is in need of collecting data on the field, data which can be supported by auxiliary information on the moment or at a later context.

----

Before using this framework there are some types of configuration files that must be understood and reproduced. These files allow not only the generation of the user interface but also the creation of abstracts for each field visit.
This system allows the user to utilize their own configuration files for the needed purposes however, there are two types of files that are mandatory and must respect a template. These files are:
 
 1. Protocols configuration file.
 1. Abstract configuration file.
 
 ## Protocols configuration files
 Protocols are the base of data collection in this system. A protocol is applied during a period of time on EOIs (Elements of Interest) and explains to the user the steps that must be done to perform an correct observation and the values that must be registered for a desired target.
 
 This configuration file is a text file that contains a JSONArray with JSONObjects comprised by the following fields:
 
  1. *name*: the protocol name or identifier.
  1. *date_min*: starting date MM/dd.
  1. *date_max*: ending data MM/dd.
  1. *eoi*: JSONObject field that contains the name and the number given to the EOIs of the protocol.
  1. *observations*: JSONArray field that indicates the observations that must be held on the EOIs.
  1. *general_data*: optional JSONArray field that indicates the data that must be registered independent of EOIs.
  
  The first three fields are direct and self explainatory. If one of the dates is missing the protocol will be applied all the year. The field *eoi* deserves a little more explanation. 
  Lets imagine that the protocol we are trying to define is applyed on twenty different trees accross a plot. In this case, this field will defined the following way:
  
  ```json
  "eoi":{
    "name":"Tree",
    "number":20
	}
  ```
  The field *observations* contains an array which stores objects like the one shown in the following piece of code. 
  Each observation is composed by:
  1. *name*: the name given to the observation.
  1. *limited_to*: optional field that restricts the observation to a desired number of EOIs. It's important to notice that the previous shown filed *general_data* has the same structure of the field *iterations* due to the fact of being EOI undependable.
  1. *helper*: optional field which purpose is to help the user understand what must be done for the observation and can be use to create UI related components like a pop-up for guiding the user.
  1. *iterations*: JSONArray that indicates a collection of values that must be registered.
  
```
  {
    "name": "Shoots",
    "limited_to": [1,2,3],    
    "helper":[...],
    "iterations": [...]
  }
```

### Helper

  Like it was mentioned before, this field is used to associate information, that explains the registration process to the user, to the observation. 
  A helper is divided by steps and, do to the JSON format nature, it's important to identify the position of each step because there's no guarantee that the array order will be maintain.
  Each step is identified by the position, a title and a message. It can be added an additional field called *extra* that is processed as JSONObject by the framework and allows the association of extra information.
  In the following example, the first step explains what the visual observation consists of and the following step is used to illustrate an image, by using the field *extra* to point to an image file on the application resources.

```json
  "helper": [
    {
      "position":1,
      "title":"Visual observation",
      "message":"In each element of interest, analyze the state of the shoots"
    },
    {
      "position":2,
      "title":"Disease identification",
      "message":"Bruises on the shoots",
      "extra":"disease.png"
    }
  ]
```

### Iterations
	
Each object belonging to this field indicates a value that is to be inserted by the user. For this purpose there were defined different data types that the value can belong to. Each data type is identified by numeric value from 0 to 6. Depending on the type, some additional fields must be filled. The following table features the different data types and each required field. Beyond these fields, the *name* field must be define to identify the value name/description.
	
Identifier | Type | Fields
---- | --------- | ---------
 0|Boolean|-
 1|Numeric|*value_type*, *units*, *min*, *max*
 2|Textual|- 
 3|Temporal|*subtype*
 4|Categorical|*values*, *unique*
 5|Count|*value_type*, *offset*
 6|Interval|*first*, *last*
 
 
 
 Below is an explanation of the fields, as well as the values they can assume:

1. *units*: textual field used to associate units to the numerical or count. Optional.
1. *value_type*: textual field used to distinguish the values between *integer* (default) and *real* numbers in numeric and count types.
1. *offset*: array of strings used to indicate the possible values in a count type, express by triples. Each triple contains the initial value, the final value, and the step between those values. If the object in the array is not recognized as a triple, it will be added as it is. For example, if the *offset* is ["(0,20,1)","20+","100+"], the count it's linear, starting at 0 with a step of 1 until it reaches 20. After that the value 20+ and 100+ can be selected.
1. *min*: used to define the minimum numerical value.
1. *max*: used to define the maximum numerical value.
1. *values*: array of strings used to define the possible values in the categorical type.
1. *unique*: boolean that restrict the selection. If true (default) then only one value can be selected, otherwise multiple can be selected. 
1. *subtype*: textual field used to define the temporal type (datetime or time).
1. *first*: array of values used to define the left domain of the interval type.
1. *last*: array of values used to define the right domain of the interval type. If omitted, it assumes the value of *first*.

### Protocol example

For a better understanding, it is presented the definition of one protocol.

```json
{
   "name":"Shoots",
   "date_min":"4/1",
   "date_max":"10/30",
   "eoi":{
      "name":"Tree",
      "number":20
   },
   "observations":[
      {
         "name":"Shoots",
         "helper":[
            {
               "position":1,
               "title":"Visual observation",
               "message":"In each element of interest, analyze the state of the shoots"
            },
            {
               "position":2,
               "title":"Disease identification",
               "message":"Bruises on the shoots",
               "extra":"disease.png"
            }
         ],
         "iterations":[
            {
               "name":"Number of affected shoots",
               "data_type":1,
               "value_type":"integer",
               "max":5,
               "min":0
            }
         ]
      }
   ]
}
```


 ## Abstract configuration files
 The abstracts are a way of synthesize all the information of one field visit into an object. The information can be divided into two groups, the data that the app stores in the database and the data that it can be computed given methods and their arguments. Therefore, the configuration file contains flags that can be turn on/off, depending on the needs of the project and it's possible to associate external methods present in the project where the framework is placed to add the returned values to the abstract object. This file must respect the following template.
```json
{
   "Flags":{
      "visit_data":true,
      "visit_info":true,
      "complementary_data":true,
      "complementary_info":true,
      "plot_data":true,
      "plot_info":true,
      "multimedia_count":true,
      "gps_info":true
   },
   "Methods":[
      {
         "package_class_name":"com.example.myapp.MyClass",
         "method_name":"myMethod",
         "args_type":[
            "java.lang.String"
         ]
      }
   ]
}
```
The first field stores the flags for the different types of information that the framework can fetch from the database. By changing one value to false, that data will not appear in the final abstract object. This flags are the following:

1. *visit_data*: boolean that indicates if the visit data (id, start time and ending time) is to be accountable in the abstract.
1. *visit_info*: boolean that indicates if the visit information (stored in the info column of the corresponding table) is to be accountable in the abstract.
1. *complementary_data*: boolean that indicates if the complementary observations data (id, start time and ending time) is to be accountable in the abstract.
1. *complementary_info*: boolean that indicates if the  complementary observations information (stored in the info column of the corresponding table) is to be accountable in the abstract.
1. *plot_data*: boolean that indicates if the plot data (id, acronym and name) is to be accountable in the abstract.
1. *plot_info*: boolean that indicates if the plot information (stored in the info column of the corresponding table) is to be accountable in the abstract.
1. *multimedia_count*: boolean that indicates if the multimedia file count is to be accountable in the abstract.

The last main field on this file stores the methods that are to be invoked during the abstract generation. Their returned values are stored in the final object mapped by the class package name and the method corresponding method name. This field is an array of objects that contains the package class name, the method signature and an array of types of arguments. In the case shown above, the method *myMethod* from *MyClass* will be called, receiving a string argument, after fetching all the data from the database (since all the flags are set to true), and it's returned value will be store in a structure that maps the value to the corresponding method name that in turn is mapped to the package class name.

