export default class UserEdit {
  public name = ''
  public email = ''
  public password = ''

  constructor(name: string = '', email: string = '', password: string = '') {
    this.name = name
    this.email = email
    this.password = password
  }
}
