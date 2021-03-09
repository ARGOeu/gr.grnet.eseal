#!/usr/bin/env python3

# Remote endpoint that mocks the behavior of a signing backend from a remote provider
# The endpoint is a simple flask app that by default listens to port 5000
# It accepts incoming requests with http POST to `host.remote.node:5000/dsa/v1/sign`
# It dumps the message properties and the decoded payload to a local file `./mock_provider_endpoint.log`
#
# To run the example endpoint issue:
#  $ export FLASK_APP=mock_Provider_endpoint.py
#  $ flask run
#
# If you want the endpoint to support https issue:
#  $ ./mock_provider_endpoint.py --cert /path/to/cert --key /path/to/cert/key
#
# You can also control the artificial delay embedded into the handler with the argument -delay, which defaults to 3 seconds
# You can also specify the bind port with the -port argument, default is 5000

from flask import Flask
import argparse
from logging.config import dictConfig
import ssl
import flask_cors
from flask.logging import default_handler
import time

dictConfig({
    'version': 1,
    'formatters': {'default': {
        'format': '[%(asctime)s] %(levelname)s in %(module)s: %(message)s',
    }},
    'handlers': {
        'wsgi': {
            'class': 'logging.StreamHandler',
            'stream': 'ext://flask.logging.wsgi_errors_stream',
            'formatter': 'default',
            'level': 'INFO'
        },
        'logfile': {
            'class': 'logging.FileHandler',
            'filename': 'mock_provider_endpoint.log',
            'formatter': 'default',
            'level': 'INFO'
        }
    },
    'root': {
        'level': 'INFO',
        'handlers': ['wsgi', 'logfile']
    }
})


app = Flask(__name__)

app.logger.removeHandler(default_handler)

ARTIFICIAL_DELAY = 0


@app.route('/dsa/v1/sign', methods=['POST'])
def sign():

    app.logger.info("Sleeping for {0} seconds.".format(ARTIFICIAL_DELAY))
    time.sleep(ARTIFICIAL_DELAY)

    response_body = {
        "Success": True,
        "Data": {"SignedFileData": "JVBERi0xLjUNCiW1tbWFT0YK"}
    }

    return response_body, 200

if __name__ == "__main__":

    parser = argparse.ArgumentParser(description="Simple flask endpoint for push subscriptions")

    parser.add_argument(
        "-cert", "--cert", metavar="STRING", help="Certificate location",
        default="/etc/grid-security/hostcert.pem", dest="cert")

    parser.add_argument(
        "-key", "--key", metavar="STRING", help="Key location",
        default="/etc/grid-security/hostkey.pem", dest="key")

    parser.add_argument(
        "-port", "--port", metavar="INTEGER", help="Bind port",
        default=5000, type=int, dest="port")

    parser.add_argument(
        "-delay", "--delay", metavar="INTEGER", help="Artificial delay to mock the processing time",
        default=3, type=int, dest="delay")

    args = parser.parse_args()

    flask_cors.CORS(app=app, methods=["OPTIONS", "HEAD", "POST"], allow_headers=["X-Requested-With", "Content-Type"])

    ARTIFICIAL_DELAY = args.delay

    context = ssl.SSLContext(ssl.PROTOCOL_TLSv1_2)
    context.load_cert_chain(args.cert, args.key)

    app.run(host='0.0.0.0', port=args.port, ssl_context=context, threaded=True, debug=True)