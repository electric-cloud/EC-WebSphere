### Delete JMS Queue

To delete JMS Queue in CloudBees CD interface, do these steps:

* Create procedure.

* Create step, choose Plugin, choose DeleteJMSQueue from procedures
picker.

* Enter the following parameters: 

![image](images/DeleteJMSQueue/ProcedureConfig.png)


After the job runs, you can view the results, including the following
job details:

![image](images/DeleteJMSQueue/ProcedureResult.png)

To delete JMS Queue in CloudBees CD interface, do these steps:

* Create Pipeline.

* Create task.

* In task definition choose Plugin and choose following parameters:

![image](images/DeleteJMSQueue/PipelinePicker.png)

* Click on arrow.

* Enter the following parameters: 

![image](images/DeleteJMSQueue/PipelineConfig.png)


After the pipeline runs, you can view the results, including the
following step details:

![image](images/DeleteJMSQueue/PipelineResult.png)

In the **DeleteJMSQueue** step, click the Log icon to see the
diagnostic information. The output is similar to the following
diagnostic report.

![image](images/DeleteJMSQueue/ProcedureLog.png)
