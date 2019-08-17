var instance = axios.create({
    baseURL: 'http://'+document.domain+'/',
    timeout: 2000,
    headers: {
        'content-type': 'application/x-www-form-urlencoded'
    }
});

var baseURL= 'http://'+document.domain+':8080/';
