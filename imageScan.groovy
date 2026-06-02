def call(String imageName) {

    sh """
    trivy image \
    --severity HIGH,CRITICAL \
    --exit-code 1 \
    ${imageName}:${BUILD_NUMBER}
    """
}
