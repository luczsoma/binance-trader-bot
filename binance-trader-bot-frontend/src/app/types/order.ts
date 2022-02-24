export class Order {
  public constructor(
    public readonly pair: string,
    public readonly quantity: number,
    public readonly price: number
  ) {}
}
