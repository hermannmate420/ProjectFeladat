export interface Ticket {
    id: number;
    userId: number;
    name: string;
    email: string;
    subject: string;
    message: string;
    createdAt: string;
    status: string;
    type?: 'ticket';
}

