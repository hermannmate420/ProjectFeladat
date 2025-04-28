import { Timestamp } from "rxjs";

export interface NewsletterSubscriber {
    selected: boolean;
    subscriberId: number;
    email: string;
    fullName: string;
    createdAt?: Timestamp<Date>;
    active?: boolean;
}