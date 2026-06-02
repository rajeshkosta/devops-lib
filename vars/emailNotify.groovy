def call(String status) {

    emailext(
        subject: "${JOB_NAME} - ${status}",
        body: """
        Job: ${JOB_NAME}
        Build: ${BUILD_NUMBER}
        Status: ${status}
        """,
        to: "devops@example.com"
    )
}
