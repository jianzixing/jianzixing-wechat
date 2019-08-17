var VERSION = "app:version:1.0.100";

var CACHE_LIST = [
    "admin/lib/",
    "admin/app/utils/",
    "admin/image/"
];

// 开始安装文件
self.addEventListener("install", function (event) {
    console.log('Service Worker: install');
    event.waitUntil(
        caches.open(VERSION)
            .then(function (cache) {
                cache.addAll(CACHE_LIST);
            })
            .then(function () {
                self.skipWaiting()
            })
    );
});

// 删除无用的缓存文件
self.addEventListener('activate', function (event) {
    console.log('Service Worker: activate');
    event.waitUntil(
        caches.keys()
            .then(function (lists) {
                return Promise.all(
                    lists.filter(function (key) {
                        return key != VERSION
                    }).map(function (key) {
                        caches.delete(key)
                    })
                )
            })
            .then(function () {
                    self.clients.claim()
                }
            )
    );
});


// 获取一个缓存中的资源，如果没有就从网络中获取然后缓存
self.addEventListener('fetch', function (event) {
    if (event.request.method !== 'GET') return;
    var url = event.request.url;
    var isLoadFromCache = false;
    for (var i in CACHE_LIST) {
        if (url.indexOf(CACHE_LIST[i]) >= 0) {
            isLoadFromCache = true;
        }
    }

    if (isLoadFromCache) {
        event.respondWith(
            caches.open(VERSION)
                .then(function (cache) {
                    return cache.match(event.request)
                        .then(function (response) {
                            if (response) {
                                console.log('cache fetch: ' + url);
                                return response;
                            }
                            return fetch(event.request)
                                .then(function (nr) {
                                    console.log('network fetch: ' + url);
                                    if (nr.ok) cache.put(event.request, nr.clone());
                                    return nr;
                                })
                                .catch(function () {
                                    // 如果缓存没有网络也没获取到就什么不做
                                });
                        });
                })
        );
    }
});
