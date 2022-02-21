import { OrderSide } from './orderSide';
import { OrderStatus } from './orderStatus';
import { OrderType } from './orderType';
import { TimeInForce } from './timeInForce';

export interface OpenOrderResponse {
  symbol: string;
  orderId: number;
  orderListId: number;
  clientOrderId: string;
  price: number;
  origQty: number;
  executedQty: number;
  cummulativeQuoteQty: number;
  status: OrderStatus;
  timeInForce: TimeInForce;
  type: OrderType;
  side: OrderSide;
  stopPrice: number;
  icebergQty: number;
  time: number;
  updateTime: number;
  isWorking: boolean;
  origQuoteOrderQty: number;
}
