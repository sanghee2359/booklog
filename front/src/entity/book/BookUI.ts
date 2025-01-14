// BookUI.ts
export default class BookUI {
  public bookId: number
  public image: string // 현재 이미지 상태

  constructor(book: any) {
    this.bookId = book.bookId
    this.image = ''
  }
}
