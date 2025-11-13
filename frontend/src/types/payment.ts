export type AdminPayment = {
  orderId: string;
  orderName: string;
  amount: number;
  method: string;
  status: string;
  approvedAt: string;
  userName: string;
  userEmail: string;
};

export type AdminPaymentSummary = {
  totalPayments: number;
  successPayments: number;
  totalRevenue: number;
};
