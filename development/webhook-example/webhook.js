const http = require('http');
http.createServer(function (req, res) {

    if (req.method === "POST") {

        var body = "";
        req.on("data", function (chunk) {
            body += chunk;
        });

        req.on("end", function(){
            try {
                console.log(JSON.parse(body));
                res.writeHead(200);
                res.end();
            } catch (e) {
                res.writeHead(500, e);
                res.end();
            }
        });
    } else {
        res.writeHead(405);
        res.end();
    }

}).listen(8000, 'localhost', () => {
    console.log('server is running on http://localhost:8000')
});
