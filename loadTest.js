import http from 'k6/http';
import { check } from 'k6';
export const options = {
  vus: 1,
  duration: '60s',
};
export default function () {

  const url = 'http://localhost:8080/hello';
//    const payload = JSON.stringify({});
//
//    const params = {
//          headers: {
//            'Content-Type': 'application/json',
//            'Accept-Encoding': 'gzip'
//          },
//    };

    //const res = http.get(url, payload, params);
    const res = http.get(url);
    //console.log(res.json());
    check(res, {
        'is status 200': (r) => r.status === 200,
      });
}
