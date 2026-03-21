const http = require('http');

const server = http.createServer((req, res) => {
  res.statusCode = 200;
  res.setHeader('Content-Type', 'text/plain');
  res.end('Hello, Docker!\n');
});

server.listen(3000, '0.0.0.0', () => {
  console.log('Server đang chạy tại cổng 3000');
});