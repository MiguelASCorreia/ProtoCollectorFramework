# ProtoCollectorFramework

This framework was developed for the purpose of favoring the creation of android mobile applications that target the issue of phytosanitary management where the user is in need of collecting data on the field, data which can be supported by auxiliary information on the moment or at a later context.

----

## Framework Overview

The framework is divided in six different modules, each on composed of a set of Java classes and, in some cases, a group of XML resources. All these modules were developed in parallel with a mobile application whose purpose was to collect information on the field regarding pets and diseases that affect apple and pear crops. That said, the design of the framework was heavily influenced by the needs of that project. However, an effort was made to generalize the essential features for, not only the phytosanitary context, but for all types of field data collection that require making observations according to elements of interest.

### Data Module

This module is the foundation of the framework. It is divided in two group, the data objects, which are used across the different modules, and the database, that was implemented in a generic way to store information according to the needs of the application. 
All the following modules make available CRUD methods that allow each one to manage the data that they are responsible for. 

The database is composed of nine tables, all with creation, edition and deletion timestamps.  These tables are:
1. *PlotTable*: Stores geographical information associated with the field plots. As an extra column named *plot_info* that allows the application to store generic information associated with the entity.
1. *VisitTable*: Stores the information associated with a field visit. The EOI data and an extra field name *visit_info* are stored as a JSON string allowing some flexibility. Has a foreign key that references the plot’s identifier from the *PlotTable*.
1. *ComplementaryTable*: Stores the information from post visit activities. Has the same structure of a visit plus a foreign key that references the visit’s identifier from the *VisitTable*.
1. *MultimediaTable*: Stores the information from captured multimedia files associated with visit entities (foreign key). Can store geographical information and an extra field name *multimedia_info* that allows the application to store generic information associated with the entity.
1. *TrajectoryTable*: Stores the trajectories from each field visit (foreign key). 
1. *TrajectorySegmentTable*: Stores the segments from each trajectory (foreign key).
1. *TrajectoryPointTable*: Stores the points from each segment (foreign key). Each point has an extra field name *point_info* that allows the application to store generic information associated with the entity.
1. *ConfigTable*: Stores the information (name, path and version) for the desired configuration files.
1. *BluetoothSyncTable*: Stores logs from the Cooperation module for each connection moment.

### Registration Module

The registration module was created for the controllers of the visits and the complementary observations that are made after them, offering CRUD methods for fetching data and creating new entities. Afterwards, it was expanded with the configuration manager and the abstract generator. 
The configurations allow the association of configurations files that can be useful for setting up the application that the developer is looking up to. Some configuration files are mandatory for the normal execution of this framework as it is the case of the protocol configuration file. The abstract generator offers a tool to generate abstracts based on the records from field visits plus returned values from methods that the developer deems necessary. It makes use of the configuration manager for defining the abstract template which will be explained after this section.

### Location Module
Location data maybe useful for data analyses and to detect some behaviors that can be linked to data collection moments. With this purpose, a location module was developed to manage all the geographical information associated with the field plots, but also the user’s location during a visit. This module was developed with the support of the Mapbox API, offering method for map manipulation like the creation of symbols, lines and polygons.
Two location listeners were made available, one for plot detection and another for user location management, however, the module can be extended with new listeners that satisfy the needs of the specific project.  In the second one, the route taken by the user is stored in the database and can be easily exported with the GPX format. This files contains all the point's information across the following tags:

1. *lat*: latitude of the given point.
1. *ln*: longitude of the given point.
1. *time*: timestamp for the given point in the format yyyy-MM-dd'T'HH:mm:ssZ.
1. *sat*: number of satellites used for the given point.
1. *ele*: altitude in meters, of the given point, above the WGS 84 reference ellipsoid.
1. *accuracy*: estimated horizontal accuracy of the given point, radial, in meters.

### Multimedia Module
Sometimes the collected data needs to be supported by some other means. For this purpose, the multimedia module was implemented so that the application allows the association of multimedia to a field visit as a whole or even to specific moments.
This module provides methods to favor the process of capturing multimedia elements such as photos and audios, managing all the needed permissions. It can be easily extended to other types of elements since each element it is stored with a type identifier. Additionally, provides a listener that allows the conversion of speech to text if there is a need to store the textual information.  An element can still be supported by the data provided by the location module and it is always marked with a creation timestamp.

### Interface Module
The automatic component generation is possible due to this module. It contains some custom views that were developed to satisfy needs that the default views were not able to and are used on the interface component API that was created. The interface components that are generated are dependent of the corresponding data type that is specified in the protocol’s configuration file, that will be explain shortly after this overview. Each component uses message mechanism that it’s triggered every time their value is changed. This mechanism sends a message with the updates in real time to a provided handler.

### Cooperation Module
This module offers a Bluetooth connection management API that sends message to the desired context to handle each on as the developer intends to. Also offers UI layouts, adapters and a broadcast receiver to handle and automate the pairing process and the search for devices.
The main purpose of this module was to enable the exchange of messages between users during the same visit, so that the work could be divided by users on the field and then the data combined into a single record, made available in both devices at the same time.

By default, this module accounts for five different types of messages that are sent by the handler to the calling activity and can be processed for the desired application. These messages are tagged by the following constants:

1. __*MESSAGE_ENABLED*__:  *BluetoothAdapter* is enabled and the application is ready to connect with another device.
1. __*MESSAGE_CONNECTED*__:  The requested connection was successful.
1. __*MESSAGE_RECEIVED*__:  Reception of a message from the partner.
1. __*MESSAGE_HOSTING*__:  This device is the host of the current connection. The host device is the on that accepts the connection.
1. __*MESSAGE_ERROR*__: An error has occurred during the initialization of the *BluetoothAdapter*, during the reception of a message or the connection was lost.

 
 ## Configuration
 Before using this framework there are some types of configuration files that must be understood and reproduced. These files allow not only the generation of the user interface but also the creation of abstracts for each field visit.
This system allows the user to utilize their own configuration files for the needed purposes however, there are two types of files that are mandatory and must respect a template. These files are:
 
 1. Protocols configuration file.
 1. Abstract configuration file.
 
 ### Protocol configuration file

 
 Protocols are the base of data collection in this system. A protocol is applied during a period of time on EOIs (Elements of Interest) and explains to the user the steps that must be done to perform an correct observation and the values that must be registered for a desired target.
 
 This configuration file is a text file that contains a JSONArray with JSONObjects comprised by the following fields:
 
  1. *name*: the protocol name or identifier.
  1. *date_min*: starting date using MM/dd format.
  1. *date_max*: ending date using MM/dd format.
  1. *eoi*: JSONObject field that contains the name and the number given to the EOIs of the protocol.
  1. *observations*: JSONArray field that indicates the observations that must be held on the EOIs.
  1. *general_data*: optional JSONArray field that indicates the data that must be registered independent of EOIs.
  
  The first three fields are direct and self explanatory. If one of the dates is missing the protocol will be applied for the entirety of given year. The field *eoi* deserves a more detailed explanation, however. 
  Lets consider that a protocol is being defined to apply to twenty different trees across a plot. As such, this field will be defined in the following way:
  
  ```json
  "eoi":{
    "name":"Tree",
    "number":20
	}
  ```
  The field *observations* contains an array which stores objects like the one shown in the following excerpt of code. 
  Each observation is composed of:
  1. *name*: the name given to the observation.
  1. *limited_to*: optional field that restricts the observation to a desired number of EOIs. It's important to notice that the previously shown filed *general_data* has the same structure the field *iterations* due to the fact of being EOI independent.
  1. *helper*: optional field whose purpose is to help the user understand what must be done for the observation and can be used to create UI related components such as pop-ups to guide the user.
  1. *iterations*: JSONArray that indicates a collection of values that must be registered.
  
```
  {
    "name": "Shoots",
    "limited_to": [1,2,3],    
    "helper":[...],
    "iterations": [...]
  }
```

#### Helper

  As mentioned above, this field is used to associate information to the observation that explains the registration process to the user.
  A helper is divided by steps and, due to the JSON format's nature, it's important to identify the position of each step because there's no guarantee that the array order will be maintained.
  Each step is identified by it's position, a title and a message. An additional field called *extra* can be added, that is processed as a JSONObject by the framework and allows the association of extra information.
  In the following example, the first step explains what the visual observation consists of and the following step is used to illustrate an image, by using the field *extra* to point to an image file on the application's resources.

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

#### Iterations
	
Each object belonging to this field indicates a value that is to be inserted by the user. For this purpose there were defined different data types that the value can belong to. Each data type is identified by a numeric value ranging from 0 to 6. Depending on the type, some additional fields may need to be filled. The following table features the different data types and each required field. Beyond these fields, the *name* field must be defined to identify the value's name/description.
	
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

1. *units*: textual field used to associate units to the numerical or count types. Optional.
1. *value_type*: textual field used to distinguish the values between *integer* (default) and *real* numbers in numeric and count types.
1. *offset*: array of strings used to indicate the possible values in a count type, expressed by triplets. Each triplets contains the initial and final values, and the step between those. If the object in the array is not recognized as a triplet, it will be added as it is. For example, if the *offset* is ["(0,20,1)","20+","100+"], the count it's linear, starting at 0 with a step of 1 until it reaches 20. After that the values  20+ and 100+ can be selected.
1. *min*: used to define the minimum numerical value.
1. *max*: used to define the maximum numerical value.
1. *values*: array of strings used to define the possible values in the categorical type.
1. *unique*: boolean that restricts the selection. If true (default) then only one value can be selected, otherwise multiple can be selected. 
1. *subtype*: textual field used to define the temporal type (datetime or time).
1. *first*: array of values used to define the left domain of the interval type.
1. *last*: array of values used to define the right domain of the interval type. If omitted, it assumes the value of *first*.

#### Protocol Specification

For a better understanding, it is presented the definition of a protocol.It is applied on twenty different trees across the field plot, between the months of April and October. In each one of the EOIs, the user must observe the state of the shoots and register the number of affected ones (up to a maximum of five). 
By specifying the protocol configuration file, which must contain a JSONArray named "Protocols", the interface module allows processing the information of each protocols in the structure,  generating the interface components as well as the data structure that maps each value to the corresponding observation and protocol.

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
         "name":"Shoots state",
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
When specifying the protocol configuration file, which must contain a JSONArray with a given tag name, the interface module allows processing the information of each protocol in the structure,  generating the interface components as well as the data structure that maps each value to the corresponding observation and protocol. Afterwards, the following structures are made available.
```java
    HashMap<String, JSONObject> protocolsByTag; // Structure that maps each protocol JSONObject to the given protocol’s name
    
    HashMap<String, Integer> numberOfEOIsPerProtocol; // Structure that maps each EOI counter to the given protocol's name
    
    SortedSet<String> hiddenProtocols; // Set of protocol's names that are not active on the given time of the year duo to the date_min and date_max specification
    
    HashMap<String, HashMap<String, List<ComponentView>>> viewsPerProtocol; // Structure that maps all the generated observation's views that are EOI dependent to the given protocol's name
    
    HashMap<String, List<ComponentView>> generalObservations; // Structure that maps all the generated observation's views that are not EOI dependent to the given protocol's name
    
    HashMap<String, HashMap<String, List<Integer>>> limitedObservations; // Structure that maps the list of accountable EOIs to the given protocol's name
    
    HashMap<String, HashMap<String, List<HelperData>>> helpersPerProtocol; // Structure that maps the list of helpers to the given protocol's name
```

 ### Abstract configuration files
 The abstracts are used to synthesize the information of one field visit into an object. It can be divided into two groups, the data that the application stores in the database and the data that can be computed given methods and their respective arguments. Therefore, the configuration file contains flags that can be turned on or off, depending on the needs of the project and it is possible to associate external methods present in the project where the framework is placed to add the returned values to the abstract object. This file must respect the following template.
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
The first field stores the flags for the different types of information that the framework can fetch from the database. By changing a flag to false, that data will not appear in the final abstract object. These flags are the following:

1. *visit_data*: boolean that indicates if the visit data (id, start time and ending time) is to be inserted into the abstract.
1. *visit_info*: boolean that indicates if the visit information (stored in the info column of the corresponding table) is to be inserted into the abstract.
1. *complementary_data*: boolean that indicates if the complementary observations data (id, start time and ending time) is to be inserted into the abstract.
1. *complementary_info*: boolean that indicates if the  complementary observations information (stored in the info column of the corresponding table) is to be inserted into the abstract.
1. *plot_data*: boolean that indicates if the plot data (id, acronym and name) is to be inserted into the abstract.
1. *plot_info*: boolean that indicates if the plot information (stored in the info column of the corresponding table) is to be inserted into the abstract.
1. *multimedia_count*: boolean that indicates if the multimedia file count is to be inserted into the abstract.

The last main field on this file stores the methods that are to be invoked during the abstract's generation. Their return values are stored in the final object mapped by the class package name and the method's corresponding method name. This field is an array of objects that contains the package class name, the method signature and an array of types of arguments. In the example above, the method *myMethod* from *MyClass* will be called, receiving a string as an argument,, after fetching the data from the database (since all the flags are set to true), and its return value will be stored in a structure that maps the value to the corresponding method name, which  in turn is mapped to the package class name.

