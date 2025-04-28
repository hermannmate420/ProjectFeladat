import { Comment } from './comment.model';

export interface BlogPost {
    blogId: number;
    title: string;
    content: string;
    author: string;
    authorId: number;
    createdAt: string;
    likesCount: number;
    postCommentId: number;
    imageUrl: string;
    image: File;
    imagesUrl: string;
}

export interface BlogWithComments extends BlogPost {
    comments: Comment[];
    editing?: boolean;
}
