### Delete JMS ConnectionFactory

To delete JMS ConnectionFactory in CloudBees CD interface, do these steps:

* Create procedure.

* Create step, choose Plugin, choose DeleteJMSConnectionFactory from procedures
picker.

* Enter the following parameters: 

![image](images/DeleteJMSConnectionFactory/ProcedureConfig.png)


After the job runs, you can view the results, including the following
job details:

![image](images/DeleteJMSConnectionFactory/ProcedureResult.png)

To delete JMS ConnectionFactory in CloudBees CD interface, do these steps:

* Create Pipeline.

* Create task.

* In task definition choose Plugin and choose following parameters:

![image](images/DeleteJMSConnectionFactory/PipelinePicker.png)

* Click on arrow.

* Enter the following parameters: 

![image](images/DeleteJMSConnectionFactory/PipelineConfig.png)


After the pipeline runs, you can view the results, including the
following step details:

![image](images/DeleteJMSConnectionFactory/PipelineResult.png)

In the **DeleteJMSConnectionFactory** step, click the Log icon to see the
diagnostic information. The output is similar to the following
diagnostic report.

![image](images/DeleteJMSConnectionFactory/ProcedureLog.png)
