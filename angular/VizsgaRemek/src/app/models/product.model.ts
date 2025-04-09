export interface Category {
    id: number;
    name: string;
}

export interface Product {
    id: number;
    name: string;
    description: string;
    slug?: string;
    metaTitle?: string;
    price: number;
    discountPrice?: number;
    stockQuanty: number;
    categoryId: number;
    status: 'active' | 'inactive';
    createdAt: string;
    category: Category;
    updatedAt?: string;
    imageUrl?: string;
    deletedAt?: string | null;
}
