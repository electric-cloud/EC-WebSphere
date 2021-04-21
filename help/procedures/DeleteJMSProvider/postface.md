### Delete JMS Provider

To delete JMS Provider in CloudBees CD interface, do these steps:

* Create procedure.

* Create step, choose Plugin, choose DeleteJMSProvider from procedures
    picker.

* Enter the following parameters: 

![image](images/DeleteJMSProvider/ProcedureConfig.png)


After the job runs, you can view the results, including the following
job details:

![image](images/DeleteJMSProvider/ProcedureResult.png)

To delete JMS Provider in CloudBees CD interface, do these steps:

* Create Pipeline.

* Create task.

* In task definition choose Plugin and choose following parameters:

![image](images/DeleteJMSProvider/PipelinePicker.png)

* Click on arrow.

* Enter the following parameters: 

![image](images/DeleteJMSProvider/PipelineConfig.png)


After the pipeline runs, you can view the results, including the
following step details:

![image](images/DeleteJMSProvider/PipelineResult.png)

In the **DeleteJMSProvider** step, click the Log icon to see the
diagnostic information. The output is similar to the following
diagnostic report.

![image](images/DeleteJMSProvider/ProcedureLog.png)
