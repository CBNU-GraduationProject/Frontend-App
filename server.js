const express = require('express');
const app = express();
const PORT = 5000;

const path = require('path');
// 서버를 5500 포트에서 시작합니다.
app.listen(PORT, () => {
    console.log(`Express server is running on port ${PORT}`);
});

app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'test.html'));
});

// 2nd.html로 가는 경로 설정
app.get('/list', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'list.html'));
});

// 2nd.html로 가는 경로 설정
app.get('/login', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'login.html'));
});

app.get('/cam', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'cam.html'));
});


// '/' 경로에 대해 Axios 요청을 처리합니다.
app.get('/api', async (req, res) => {
    try {
        const url = 'https://heron-good-curiously.ngrok-free.app/';
        const response = await axios.get(url, {
            headers: {
                'ngrok-skip-browser-warning': 'true',
            }
        });

        if (response.status === 200) {
            res.send(response.data);
        } else {
            throw new Error(`Unexpected response status: ${response.status}`);
        }
    } catch (error) {
        console.error('Error fetching data:', error.response ? error.response.data : error.message);
        res.status(500).send('Error fetching data from external API');
    }
});

