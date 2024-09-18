import axios from 'axios'

const API_URL = 'http://localhost:8080/api/books'

export interface Book {
  id: number
  title: string
  author: string
  startDate?: string | null
  endDate?: string | null
  price: number
  status: 'NOT_STARTED' | 'READING' | 'COMPLETED'
}

export async function createBook(book: Book): Promise<Book> {
  const response = await axios.post<Book>(API_URL, book)
  return response.data
}

export async function getBooks(): Promise<Book[]> {
  const response = await axios.get<Book[]>(API_URL)
  return response.data
}
