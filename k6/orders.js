import http from 'k6/http';

// const baseurl = __ENV.HOST_URL;
const baseurl = "http://localhost:8080";

const params = {
  headers: {
    'Content-Type': 'application/json',
  },
};

const createOrder = () => {
  const url = `${baseurl}/api/orders/`;
  const response = http.post(url, {}, params);
  const body = JSON.parse(response.body);
  return body.id;
}

export default function () {
  createOrder();
}
