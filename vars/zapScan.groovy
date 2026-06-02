def call(String targetUrl) {

    sh """
    docker run --rm \
    -v \$(pwd):/zap/wrk/:rw \
    ghcr.io/zaproxy/zaproxy:stable \
    zap-baseline.py \
    -t ${targetUrl} \
    -r zap-report.html
    """
}
