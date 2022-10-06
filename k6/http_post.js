import http from 'k6/http';

const baseurl = __ENV.HOST_URL;

const params = {
  headers: {
    'Content-Type': 'application/json',
  },
};

const createAuthor = () => {
  const url = `${baseurl}/api/authors/`;
  const response = http.post(url, {}, params);
  const body = JSON.parse(response.body);
  return body.id;
}

const createBook = (authorId) => {
  const url = `${baseurl}/api/books/`;
  const data = JSON.stringify({ authorId: authorId });
  const response = http.post(url, data, params);
  const body = JSON.parse(response.body);
  return body.id;
}

export default function () {
  const authorId = createAuthor();
  createBook(authorId);
}
