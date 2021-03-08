import cx_Oracle
from flask import Flask, request, session, jsonify
import bcrypt, random

app = Flask(__name__)

con = cx_Oracle.connect('TMM/Password@127.0.0.1/XE')

@app.route('/ntcheck', methods=['POST'])
def ntcheck():
    data_set = {"NTNotFound": 'true', "NTAlreadyUsed": 'true'}
    jsondata = request.json
    data_set["NumeroTessera"] = jsondata["NumeroTessera"]
    cur = con.cursor()
    cur.execute("select Numero_Tessera from Cliente_Tesserato where Numero_Tessera = :NTess", NTess=data_set["NumeroTessera"])
    res = cur.fetchone()
    cur.close()
    if res is not None:
        data_set["NTNotFound"] = 'false'
        cur = con.cursor()
        cur.execute("select Numero_Tessera from Login where Numero_Tessera = :NTess", NTess=data_set["NumeroTessera"])
        res = cur.fetchone()
        cur.close()
        if res is None:
            data_set["NTAlreadyUsed"] = 'false'
    con.commit()
    return data_set

@app.route('/signupform', methods=['POST'])
def signupform():
    jsondata = request.json
    cur = con.cursor()
    cur.execute("select username from Login where username=:UN", UN=jsondata["username"])
    res = cur.fetchone()
    cur.close()
    if res:
        jsondata["username"] = "utilizzato"
        return jsondata
    else:
        hashpass = bcrypt.hashpw(jsondata["password"].encode('utf-8'), bcrypt.gensalt())
        cur = con.cursor()
        cur.execute("insert into Login (username, password, Numero_Tessera) values(:UN,:PW, :NT)", UN=jsondata["username"], PW=hashpass, NT=jsondata["numerotessera"])
        cur.close()
        con.commit()
        session['username'] = jsondata["username"]
        con.commit()
        return jsondata

@app.route("/home", methods=['POST'])
def home():
    jsondata = request.json
    if 'username' in session:
        jsondata['check'] = "true"
        jsondata['username'] = session['username']
        return jsondata
    else:
        jsondata['check'] = "false"
        return jsondata

@app.route("/newvolumes", methods=['POST'])
def newvolumes():
    jsondata = []
    cur = con.cursor()
    cur.execute("select titolo , numero, nomea, TO_CHAR(data_di_pubblicazione,'DD-MON-YYYY') as data_di_pubblicazione, prezzo_finalev, Rimanenti from Volume inner join Volumi_Disponibili on Volume.ISBN=Volumi_Disponibili.ISBN where data_di_pubblicazione > sysdate-30 AND quartiere=:QT",QT=request.headers.get('quartiere'))
    row_headers = [x[0] for x in cur.description]
    res = cur.fetchall()
    cur.close()
    for data in res:
        jsondata.append(dict(zip(row_headers,data)))
    jsonsent = jsonify(jsondata)
    con.commit()
    return jsonsent

@app.route("/personalvolumes", methods=['POST'])
def personalvolumes():
    jsondata = []
    cur = con.cursor()
    cur.execute("select numero_tessera from login where username=:NT", NT=request.headers.get('user'))
    res = cur.fetchone()
    cur.close()
    cur = con.cursor()
    cur.execute(
        "select titolo , numero, nomea, TO_CHAR(data_di_pubblicazione,'DD-MON-YYYY') as data_di_pubblicazione, prezzo_finalev, Rimanenti from Volume inner join Volumi_Disponibili on Volume.isbn=Volumi_Disponibili.isbn where Volume.isbn not in "
        "(select volume.isbn from volume inner join contienev on volume.isbn=contienev.isbn inner join carrello on contienev.codice_ordine=carrello.codice_ordine where numero_tessera=:NT) "
        "and titolo in (select titolo from volume inner join contienev on volume.isbn=contienev.isbn inner join carrello on contienev.codice_ordine=carrello.codice_ordine where numero_tessera=:NT) AND Quartiere=:QT order by numero", NT=res[0], QT=request.headers.get('quartiere'))
    row_headers = [x[0] for x in cur.description]
    res = cur.fetchall()
    cur.close()
    for data in res:
        jsondata.append(dict(zip(row_headers, data)))
    jsonsent = jsonify(jsondata)
    con.commit()
    return jsonsent

@app.route("/quartieri", methods=['POST'])
def quartieri():
    jsondata = []
    cur = con.cursor()
    cur.execute("select quartiere from Punto_Vendita")
    row_headers = [x[0] for x in cur.description]
    res = cur.fetchall()
    cur.close()
    for data in res:
        jsondata.append(dict(zip(row_headers, data)))
    jsonsent = jsonify(jsondata)
    con.commit()
    return jsonsent

@app.route("/allvolumes", methods=['POST'])
def allvolumes():
    jsondata = []
    cur = con.cursor()
    cur.execute("select titolo , numero, nomea, TO_CHAR(data_di_pubblicazione,'DD-MON-YYYY') as data_di_pubblicazione, prezzo_finalev, rimanenti from Volume inner join Volumi_Disponibili on Volume.ISBN=Volumi_Disponibili.ISBN where quartiere=:QT order by titolo", QT=request.headers.get('quartiere'))
    row_headers = [x[0] for x in cur.description]
    res = cur.fetchall()
    cur.close()
    for data in res:
        jsondata.append(dict(zip(row_headers, data)))
    jsonsent = jsonify(jsondata)
    con.commit()
    return jsonsent

@app.route("/acquista", methods=['POST'])
def acquista():
    jsondata = request.json
    random_number = random.randint(1111111111111, 9999999999999)
    totale=0
    for data in jsondata:
        totale += data['prezzo']
    tipologia = "Online"
    cur = con.cursor()
    cur.execute("select numero_tessera from login where username=:NT", NT=request.headers.get('user'))
    res = cur.fetchone()
    cur.close()
    cur = con.cursor()
    cur.execute("Insert into Carrello (CODICE_ORDINE,NUMERO_TESSERA,QUARTIERE,DATA_ACQUISTO,TOTALE,TIPOLOGIA_ACQUISTO) values (:CO,:NT,:QT,sysdate,:TOT,:TA)", CO=random_number, NT=res[0], QT=request.headers.get('quartiere'), TOT=totale, TA=tipologia)
    cur.close()
    for data in jsondata:
        cur = con.cursor()
        cur.execute("Select ISBN from Volume where Titolo=:TT and Numero=:NM",TT=data['titolo'], NM=data['numero'])
        res = cur.fetchone()
        cur.close()
        cur = con.cursor()
        cur.execute("Insert into ContieneV (ISBN, CODICE_ORDINE, QUANTITAV) values (:ISBN,:CO,1)", ISBN=res[0], CO=random_number)
        cur.close()
    jsonsent = jsonify(jsondata)
    con.commit()
    return jsonsent

@app.route("/login", methods=['POST'])
def login():
    jsondata = request.json
    cur = con.cursor()
    cur.execute("select username,password from Login where username=:UN", UN=jsondata["username"])
    res = cur.fetchone()
    cur.close()
    if res:
        if bcrypt.hashpw(jsondata["password"].encode('utf-8'), res[1]) == res[1]:
            session['username'] = jsondata["username"]
            return jsondata
    jsondata["username"]="failed"
    jsondata["password"]="failed"
    con.commit()
    return jsondata

@app.route("/logout", methods=['POST'])
def logout():
    jsondata = request.json
    session.pop('username')
    return jsondata

if __name__ == '__main__':
    app.run()
app.secret_key = 'super secret key'