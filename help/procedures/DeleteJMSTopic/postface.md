### Delete JMS Topic

To delete JMS Topic in CloudBees CD interface, do these steps:

* Create procedure.

* Create step, choose Plugin, choose DeleteJMSTopic from procedures
    picker.

* Enter the following parameters: 

![image](images/DeleteJMSTopic/ProcedureConfig.png)


After the job runs, you can view the results, including the following
job details:

![image](images/DeleteJMSTopic/ProcedureResult.png)

To delete JMS Topic in CloudBees CD interface, do these steps:

* Create Pipeline.

* Create task.

* In task definition choose Plugin and choose following parameters:

![image](images/DeleteJMSTopic/PipelinePicker.png)

* Click on arrow.

* Enter the following parameters: 

![image](images/DeleteJMSTopic/PipelineConfig.png)


After the pipeline runs, you can view the results, including the
following step details:

![image](images/DeleteJMSTopic/PipelineResult.png)

In the **DeleteJMSTopic** step, click the Log icon to see the
diagnostic information. The output is similar to the following
diagnostic report.

![image](images/DeleteJMSTopic/ProcedureLog.png)
