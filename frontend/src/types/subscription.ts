export type Subscription = {
    billingKey: string,
    customerKey : string,
    subscriptionType: "BASIC" | "PREMIUM",
    isActive: boolean;
    startDate: string;
    nextBillingDate: string | null;
    price: number;
  };

