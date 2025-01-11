export default class List<T> {
  public items: T[] = []

  constructor(items: T[] = []) {
    this.items = items
  }
  public setItems(items: T[]) {
    this.items = items
  }
  public getCount(): number {
    return this.items.length
  }
}
