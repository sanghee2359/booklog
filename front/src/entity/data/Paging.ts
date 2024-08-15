export default class Paging {
  public page = 0
  public size = 0
  public totalCount = 0
  public items: T[] = []

  public setItems(items: T[]) {
    this.items = items
  }
}
