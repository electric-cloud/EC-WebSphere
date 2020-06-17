<h3>Delete JMS Queue</h3>
                <p>To delete JMS Queue in CloudBees CD interface, do these steps:</p>
                <ol>
                    <li>Create procedure.</li>
                    <li>Create step, choose Plugin, choose DeleteJMSQueue from procedures
                    picker.</li>
                    <li>Enter the following parameters: </li>
                    <img src="../../plugins/EC-WebSphere/images/DeleteJMSQueue/ProcedureConfig.png" />
                </ol>
                <p>After the job runs, you can view the results, including the following
                job details:</p>
                <img src="../../plugins/EC-WebSphere/images/DeleteJMSQueue/ProcedureResult.png" />
                <p>To delete JMS Queue in CloudBees CD interface, do these steps:</p>
                <ol>
                    <li>Create Pipeline.</li>
                    <li>Create task.</li>
                    <li>In task definition choose Plugin and choose following parameters:
                    <p><img src="../../plugins/EC-WebSphere/images/DeleteJMSQueue/PipelinePicker.png" /></p>
                    </li>
                    <li>Click on arrow.</li>
                    <li>Enter the following parameters: </li>
                    <img src="../../plugins/EC-WebSphere/images/DeleteJMSQueue/PipelineConfig.png" />
                </ol>
                <p>After the pipeline runs, you can view the results, including the
                following step details:</p>
                <img src="../../plugins/EC-WebSphere/images/DeleteJMSQueue/PipelineResult.png" />
                <p>In the <b>DeleteJMSQueue</b> step, click the Log icon to see the
                diagnostic information. The output is similar to the following
                diagnostic report.</p>
                <img src="../../plugins/EC-WebSphere/images/DeleteJMSQueue/ProcedureLog.png" />