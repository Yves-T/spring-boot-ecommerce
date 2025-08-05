import { Addres } from './addres';
import { Customer } from './customer';
import { Order } from './order';
import { OrderItem } from './order-item';

export class Purchase {
  public customer: Customer | undefined;
  public shippingAddress: Addres | undefined;
  public billingAddress: Addres | undefined;
  public order: Order | undefined;
  public orderItems: OrderItem[] = [];
  constructor() {}
}
