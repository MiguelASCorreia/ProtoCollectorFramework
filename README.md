# ProtoCollectorFramework

This framework was developed for the purpose of favoring the creation of android mobile application that target the issue of phytosanitary management where the user is in need of collecting data on the field, data which can be supported by auxiliary information on the moment or at a later context.

----

Before using this framework there are some types of configuration files that you must understand and reproduce. This files allow no only the generation of the user interface but also the creation of abstracts for each field visit.
This system allows the user to utilize his own configuration files for the needed purposes however, there are two types of files that are mandatory and must respect a certain template. This files are the following:
 
 1. Protocols configuration file.
 1. Abstract configuration file.
 
 ## Protocols configuration files
 Protocols are the base of data collection in this system. A protocol is applyied a certain time of the year on EOIs (Elements of Interest) and explains to the user the steps that must be done to perform an correct observation and the values that must be registered for a desired target.
 
 This configuration file is a text file that contains a JSONArray with JSONObjects formed by the following fields:
 
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
	
Each object belonging to this field indicates a value that is to be inserted by the user. For this purpose there were defined different data types that the value can belong to. Each data type is identified by numeric value from 0 to 6. Depending on the type, some additional fields must be filled. The following table features the different data types and each required field.
	
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


 ## Abstract configuration files
