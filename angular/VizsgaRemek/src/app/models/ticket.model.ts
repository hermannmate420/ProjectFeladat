export interface Ticket {
    id: number;
    name: string;
    email: string;
    subject: string;
    message: string;
    createdAt: string;
    isRead: boolean;
    type?: 'ticket';
}

