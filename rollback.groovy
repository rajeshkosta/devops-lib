def call(String namespace="prod") {

    sh """
    kubectl rollout undo deployment/app \
    -n ${namespace}
    """
}
