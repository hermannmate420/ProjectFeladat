export interface Category {
    id: number;
    categoryName: string;
}

export interface Product {
    id: number;
    name: string;
    description: string;
    slug?: string;
    metaTitle?: string;
    price: number;
    fullPrice: number;
    discountPrice?: number;
    stockQuanty: number;
    status: 'active' | 'inactive';
    createdAt: string;
    category: Category;
    updatedAt?: string;
    imageUrl?: string;
    deletedAt?: string | null;
}
