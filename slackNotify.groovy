def call(String status) {

    slackSend(
        channel: '#devops',
        color: status == "SUCCESS" ? "good" : "danger",
        message: "Job ${JOB_NAME} #${BUILD_NUMBER} ${status}"
    )
}
