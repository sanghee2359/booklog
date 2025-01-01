export default class Paging {
  public page = 0
  public size = 0
  public totalCount = 0
  public items: T[] = []
  public hasNextPage = false // 무한 스크롤에 필요한 필드

  // 총 아이템 수 설정
  public setTotalCount(totalCount: number) {
    this.totalCount = totalCount
  }
  public setItems(items: T[]) {
    this.items = items
  }
  public setHasNextPage(hasNextPage: boolean) {
    this.hasNextPage = hasNextPage
  }
}
