from flask import Flask, render_template, request

app = Flask(__name__)

@app.route('/', methods=['GET'])
def index():
    print("here")
    return render_template('index.html')

@app.route('/', methods=['POST'])
def post():
    print("here")
    return "recived: {}".format(request.form)

if __name__ == "__main__":
    app.run(debug=True)
