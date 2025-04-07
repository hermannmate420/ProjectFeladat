export interface Category {
    id: number;
    name: string;
}

export interface Product {
    productId: number;
    name: string;
    description: string;
    price: number;
    stockQuanty: number;
    productPicture: string;
    categoryId: number;
    category: Category;
}
