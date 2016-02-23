export default class Complex {
  constructor(public real:number, public im:number) {

  }

  add(other: Complex) : Complex {
    return new Complex( other.real + this.real, other.im + this.im);
  }

  multiply(other: Complex) : Complex {
    return new Complex( other.real * this.real - other.im * this.im, other.real * this.im + other.im * this.real);
  }
}
