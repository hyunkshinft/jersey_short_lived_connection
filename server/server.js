const https = require('https');
const fs = require('fs');
const zlib = require('zlib');
const http = require('http');
var seq = 0

var server = function (req, res) {
  var filename = req.url.split('/')[1]
  console.log(req.url + " - " + filename)
  res.setHeader('Content-Type', 'application/json; charset=UTF-8');
  res.setHeader('Connection', 'keep-alive');
  res.setHeader('Status', '200 OK');
  res.setHeader('Cache-Control', 'max-age=0, private, must-revalidate');
  res.setHeader('Vary', 'Accept-Encoding');
  res.setHeader('Transfer-Encoding', 'chunked');

  var gzip = false
  if (filename.endsWith('.gz')) {
    gzip = true
    filename = filename.substring(0, filename.indexOf('.gz'))
    res.setHeader('Content-Encoding', 'gzip');
  }

  var read = fs.readFile
  if (filename == 'example.json') {
    read = function(file, encoding, callback) {
      json = { foo: seq++, bar: 1 }
      json = JSON.stringify(json)
      for (i = 0; i < 200; i++)
        json += '                                                                                                    '
      callback(null, json)
    }
  }

  read('/root/' + filename, { encoding: 'utf8' }, function (err, data) {
    if (gzip) {
      zlib.gzip(data, function (_, data) { 
        res.end(data);
      });
    }
    else {
      res.end(data)
    }
  });
}

const options = {
	  key: fs.readFileSync('key.pem'),
	  cert: fs.readFileSync('cert.pem')
};
http.createServer(server).listen(80);
https.createServer(options, server).listen(88);
http.createServer(server).listen(89);
http.createServer(server).listen(99);
