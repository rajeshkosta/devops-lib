def call(String imageName) {

    sh """
    kubectl set image deployment/app \
    app=${imageName}:${BUILD_NUMBER} \
    -n prod
    """
}
