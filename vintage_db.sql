-- phpMyAdmin SQL Dump
-- version 5.1.2
-- https://www.phpmyadmin.net/
--
-- Gép: localhost:3306
-- Létrehozás ideje: 2025. Ápr 28. 20:23
-- Kiszolgáló verziója: 5.7.24
-- PHP verzió: 8.3.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Adatbázis: `vintage_db`
--
CREATE DATABASE IF NOT EXISTS `vintage_db` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `vintage_db`;

DELIMITER $$
--
-- Eljárások
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `addCategories` (IN `nameIN` VARCHAR(100), IN `descIN` VARCHAR(150))   INSERT INTO categories (`categories`.`name`,`categories`.`description`)
VALUES (nameIN,descIN)$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `addTicket` (IN `p_user_id` INT, IN `p_ticket_body` VARCHAR(255), IN `p_status` VARCHAR(255))   BEGIN
    INSERT INTO tickets(user_id, ticket_body, status, created_at)
    VALUES(p_user_id, p_ticket_body, p_status, NOW());
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `changePassword` (IN `userIdIN` INT(8), IN `newPasswordIN` VARCHAR(255), IN `creatorIN` INT(100))   BEGIN
    UPDATE `user`
    SET `password` = SHA1(newPasswordIN)
    WHERE `id` = userIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `createBlog` (IN `p_title` VARCHAR(255), IN `p_content` TEXT, IN `p_author` VARCHAR(255), IN `p_author_id` INT, IN `p_likes_count` INT, IN `p_post_comment_id` INT, IN `p_image_url` VARCHAR(255), OUT `p_blog_id` INT)   BEGIN
    INSERT INTO blogs(title, content, author, author_id, likes_count, post_comment_id, image_url, created_at)
    VALUES(p_title, p_content, p_author, p_author_id, p_likes_count, p_post_comment_id, p_image_url, NOW());
    SET p_blog_id = LAST_INSERT_ID();
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `createComment` (IN `p_blog_id` INT, IN `p_comment_text` TEXT, IN `p_commenter` VARCHAR(255))   BEGIN
    INSERT INTO comments(blog_id, comment_text, commenter, created_at)
    VALUES(p_blog_id, p_comment_text, p_commenter, NOW());
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `createMessage` (IN `p_full_name` VARCHAR(255), IN `p_message` VARCHAR(1000), IN `p_email` VARCHAR(255), IN `p_subject` VARCHAR(255))   BEGIN
    INSERT INTO messages(full_name, message, email, Subject, created_at)
    VALUES(p_full_name, p_message, p_email, p_subject, NOW());
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `createShippingAddress` (IN `p_user_id` INT, IN `p_recipient_name` VARCHAR(255), IN `p_address_line1` VARCHAR(255), IN `p_address_line2` VARCHAR(255), IN `p_city` VARCHAR(100), IN `p_state` VARCHAR(100), IN `p_postal_code` VARCHAR(20), IN `p_country` VARCHAR(100), IN `p_phone` VARCHAR(50))   BEGIN
    INSERT INTO shipping_addresses(
        user_id, 
        recipient_name, 
        address_line1, 
        address_line2, 
        city, 
        state, 
        postal_code, 
        country, 
        phone, 
        created_at
    )
    VALUES(
        p_user_id, 
        p_recipient_name, 
        p_address_line1, 
        p_address_line2, 
        p_city, 
        p_state, 
        p_postal_code, 
        p_country, 
        p_phone, 
        NOW()
    );
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `create_product` (IN `nameIN` VARCHAR(255), IN `slugIN` VARCHAR(255), IN `meta_titleIN` VARCHAR(255), IN `descriptionIN` TEXT, IN `priceIN` DECIMAL(10,2), IN `discount_priceIN` DECIMAL(10,2), IN `stockIN` INT, IN `statusIN` ENUM('active','inactive','archived'), IN `image_url` VARCHAR(255), IN `categoryIN` INT(11))   BEGIN
    DECLARE categoryId INT;

    -- Kategória ID kinyerése a 'categories' táblából a megadott név alapján
    SELECT `categories`.`category_id`
      INTO categoryId
      FROM `categories`
     WHERE `categories`.`category_id` = categoryIN
     LIMIT 1;

    -- Ha nincs találat, hibát dobunk
    IF categoryId IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Kategória nem található';
    END IF;

    -- Termék beszúrása a 'products' táblába
    INSERT INTO `products`
    (
        `products`.`name`,
        `products`.`slug`,
        `products`.`meta_title`,
        `products`.`description`,
        `products`.`price`,
        `products`.`discount_price`,
        `products`.`stock_quanty`,
        `products`.`status`,
        `products`.`category_id`,
        `products`.`image_url`,
        `products`.`created_at`,
        `products`.`updatedAt`
    )
    VALUES
    (
        nameIN,
        slugIN,
        meta_titleIN,
        descriptionIN,
        priceIN,
        discount_priceIN,
        stockIN,
        statusIN,
        categoryIN,
        image_url,
        CURRENT_TIMESTAMP,     -- created_at
        CURRENT_TIMESTAMP      -- updated_at
    );
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteBlog` (IN `p_blog_id` INT)   BEGIN
    DELETE FROM blogs WHERE blog_id = p_blog_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteComment` (IN `p_comment_id` INT)   BEGIN
    DELETE FROM comments WHERE comment_id = p_comment_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteMessage` (IN `p_message_id` INT)   BEGIN
    DELETE FROM messages WHERE message_id = p_message_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteProduct` (IN `product_id` INT(11))   BEGIN
    DECLARE v_status VARCHAR(50);

    -- Lekérdezzük a termék aktuális státuszát
    SELECT `products`.`status`
      INTO v_status
      FROM `products`
     WHERE `products`.`product_id` = product_id
     LIMIT 1;

    -- Csak archived státusz esetén engedélyezzük a soft delete-et
    IF v_status != 'archived' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Csak archived termékek esetén lehet soft delete-et végrehajtani';
    ELSE
        -- Soft delete: a deleted_at mező beállítása, valamint a status és updated_at frissítése
        UPDATE `products`
        SET status = 'deleted',      -- opcionális, ha szeretnéd jelezni a soft delete-et a status mezőben
            `products`.`deleted_at` = CURRENT_TIMESTAMP,
            `products`.`updatedAt` = CURRENT_TIMESTAMP
        WHERE `products`.`product_id` = product_id;
    END IF;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteShippingAddress` (IN `p_shipping_address_id` INT)   BEGIN
    DELETE FROM shipping_addresses 
    WHERE shipping_address_id = p_shipping_address_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteTicket` (IN `p_ticket_id` INT)   BEGIN
    DELETE FROM tickets WHERE ticket_id = p_ticket_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `findUserByEmail` (IN `p_email` VARCHAR(255))   BEGIN
    SELECT `user`.`id`, `user`.`firstname` FROM user WHERE `user`.`email` = p_email LIMIT 1;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllBlogs` ()   BEGIN
    SELECT * FROM blogs;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllComments` ()   BEGIN
    SELECT * FROM comments;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllMessages` ()   BEGIN
    SELECT * FROM messages;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllProduct` ()   sELECT products.`product_id`, products.name, products.slug, products.meta_title, products.description, products.price, products.discount_price, products.`stock_quanty`, products.status, products.category_id, categories.`name` AS category_name,  products.created_at, products.`updatedAt`, products.image_url, products.deleted_at
FROM products
LEFT JOIN categories ON products.category_id = categories.category_id$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllShippingAddresses` ()   BEGIN
    SELECT * 
    FROM shipping_addresses;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllTickets` ()   BEGIN
    SELECT * FROM tickets;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllUser` ()   SELECT * FROM `user`$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getBlogById` (IN `p_blog_id` INT)   BEGIN
    SELECT * FROM blogs WHERE blog_id = p_blog_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getCommentsByBlogId` (IN `p_blog_id` INT)   BEGIN
    SELECT * FROM comments WHERE blog_id = p_blog_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getDeletedUserInfo` (IN `p_email` VARCHAR(255))   BEGIN
    SELECT deleted_at FROM user WHERE email = p_email AND is_deleted = TRUE LIMIT 1;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getMessageById` (IN `p_message_id` INT)   BEGIN
    SELECT * FROM messages WHERE message_id = p_message_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getProductById` (IN `idIN` INT(8))   SELECT *
FROM `products` 
WHERE idIN = `products`.`product_id`$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getShippingAddressById` (IN `p_shipping_address_id` INT)   BEGIN
    SELECT * 
    FROM shipping_addresses 
    WHERE shipping_address_id = p_shipping_address_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getTicketById` (IN `p_ticket_id` INT)   BEGIN
    SELECT * FROM tickets WHERE ticket_id = p_ticket_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserById` (IN `idIN` INT(8))   SELECT * FROM `user`
WHERE `user`.`id` = idIN$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `incrementBlogLikes` (IN `p_blog_id` INT)   BEGIN
    UPDATE blogs
    SET likes_count = likes_count + 1
    WHERE blog_id = p_blog_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `isUserExists` (IN `emailIN` VARCHAR(100), OUT `resultOUT` BOOLEAN)   BEGIN
DECLARE user_count INT;

    SELECT COUNT(*) INTO user_count 
    FROM `user` 
    WHERE `email` = emailIN;

    IF user_count > 0 THEN
        SET resultOUT = TRUE;
    ELSE
        SET resultOUT = FALSE;
    END IF;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `login` (IN `emailIN` VARCHAR(100), IN `passwordIN` VARCHAR(100))   SELECT * FROM `user`
    WHERE `user`.`email` = emailIN
      AND `user`.`password` = sha1(passwordIN)
      AND `user`.`is_deleted` = FALSE
    LIMIT 1$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `reactivateUser` (IN `p_user_id` INT)   BEGIN
    UPDATE user
    SET
        is_deleted = FALSE,
        deleted_at = NULL
    WHERE id = p_user_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `registerAdmin` (IN `userIN` VARCHAR(100), IN `firstnameIN` VARCHAR(100), IN `lastnameIN` VARCHAR(100), IN `emailIN` VARCHAR(100), IN `phoneIN` VARCHAR(100), IN `passwordIN` VARCHAR(100))   INSERT INTO `user`(`user`.`username`, `user`.`firstname`, `user`.`lastname`,`user`.`email`, `user`.`phone_number`, `user`.`password`, `user`.`is_admin`, `user`.`is_deleted`) VALUES (userIN, firstnameIN, lastnameIN,emailIN, phoneIN, SHA1(passwordIN),1,0)$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `registration` (IN `userIN` VARCHAR(100), IN `firstnameIN` VARCHAR(100), IN `lastnameIN` VARCHAR(100), IN `emailIN` VARCHAR(100), IN `phoneIN` VARCHAR(100), IN `passwordIN` VARCHAR(200))   INSERT INTO `user`(`user`.`username`, `user`.`firstname`, `user`.`lastname`,`user`.`email`, `user`.`phone_number`, `user`.`password`, `user`.`is_admin`, `user`.`is_deleted`) VALUES (userIN, firstnameIN, lastnameIN,emailIN, phoneIN, SHA1(passwordIN),0,0)$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateBlog` (IN `p_blog_id` INT, IN `p_title` VARCHAR(255), IN `p_content` TEXT, IN `p_author` VARCHAR(255), IN `p_author_id` INT, IN `p_likes_count` INT, IN `p_post_comment_id` INT, IN `p_image_url` VARCHAR(255))   BEGIN
    UPDATE blogs
    SET 
        title = p_title,
        content = p_content,
        author = p_author,
        author_id = p_author_id,
        likes_count = p_likes_count,
        post_comment_id = p_post_comment_id,
        image_url = p_image_url
    WHERE blog_id = p_blog_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateComment` (IN `p_comment_id` INT, IN `p_comment_text` TEXT)   BEGIN
    UPDATE comments
    SET comment_text = p_comment_text
    WHERE comment_id = p_comment_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateProduct` (IN `productIN` INT(11), IN `nameIN` VARCHAR(255), IN `slugIN` VARCHAR(255), IN `meta_titleIN` VARCHAR(255), IN `descriptionIN` TEXT, IN `priceIN` DECIMAL(10,2), IN `discount_priceIN` DECIMAL(10,2), IN `stock_quantityIN` INT, IN `statusIN` ENUM('active','inactive','archived'), IN `categoryIN` INT(11), IN `image_urlIN` VARCHAR(255))   BEGIN
    DECLARE categoryId INT;
    -- Termék adatainak frissítése a 'products' táblában
    UPDATE products
    SET
        `products`.`name` = nameIN,
        `products`.`slug` = slugIN,
        `products`.`meta_title` = meta_titleIN,
        `products`.`description` = descriptionIN,
        `products`.`price` = priceIN,
        `products`.`discount_price` = discount_priceIN,
        `products`.`stock_quanty` = stock_quantityIN,
       	`products`.`status` = statusIN,
       	`products`.`category_id` = categoryIN,
        `products`.`image_url` = image_urlIN,
        `products`.`updatedAt` = CURRENT_TIMESTAMP
    WHERE `products`.`product_id` = productIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateShippingAddress` (IN `p_shipping_address_id` INT, IN `p_user_id` INT, IN `p_recipient_name` VARCHAR(255), IN `p_address_line1` VARCHAR(255), IN `p_address_line2` VARCHAR(255), IN `p_city` VARCHAR(100), IN `p_state` VARCHAR(100), IN `p_postal_code` VARCHAR(20), IN `p_country` VARCHAR(100), IN `p_phone` VARCHAR(50))   BEGIN
    UPDATE shipping_addresses
    SET 
        user_id = p_user_id,
        recipient_name = p_recipient_name,
        address_line1 = p_address_line1,
        address_line2 = p_address_line2,
        city = p_city,
        state = p_state,
        postal_code = p_postal_code,
        country = p_country,
        phone = p_phone,
        updated_at = NOW()
    WHERE shipping_address_id = p_shipping_address_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateTicket` (IN `p_ticket_id` INT, IN `p_new_status` VARCHAR(255))   BEGIN
    UPDATE tickets SET status = p_new_status WHERE ticket_id = p_ticket_id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateUser` (IN `modifierId` INT, IN `targetUserId` INT, IN `newUsername` VARCHAR(255), IN `newFirstname` VARCHAR(255), IN `newLastname` VARCHAR(255), IN `newEmail` VARCHAR(255), IN `newPhoneNumber` VARCHAR(255), IN `newIsAdmin` BOOLEAN)   BEGIN
    DECLARE permissionError BOOLEAN DEFAULT FALSE;
    DECLARE modifierIsAdmin BOOLEAN DEFAULT FALSE;

-- Ellenőrizzük, admin-e a módosító
	SELECT `user`.`is_admin` INTO modifierIsAdmin
	FROM user
	WHERE `user`.`id` = modifierId;

    IF modifierIsAdmin = FALSE AND modifierId != targetUserId THEN
        SET permissionError = TRUE;
    END IF;

    IF permissionError THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Permission denied: only admins can update other users.';
    ELSE
        IF modifierIsAdmin THEN
            UPDATE user
            SET
                `user`.`username` = newUsername,
                `user`.`firstname` = newFirstname,
                `user`.`lastname` = newLastname,
                `user`.`email` = newEmail,
                `user`.`phone_number` = newPhoneNumber,
                `user`.`is_admin` = newIsAdmin
            WHERE `user`.`id` = targetUserId;
        ELSE
            UPDATE user
            SET
                `user`.`username` = newUsername,
                `user`.`firstname` = newFirstname,
                `user`.`lastname` = newLastname,
                `user`.`email` = newEmail,
                `user`.`phone_number` = newPhoneNumber
            WHERE `user`.`id` = targetUserId;
        END IF;
    END IF;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `userDelete` (IN `userIN` INT(11))   UPDATE `user`
    SET 
        `user`.`is_deleted` = TRUE,
        `user`.`deleted_at` = NOW()
    WHERE `user`.`id` = userIN$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `blogs`
--

CREATE TABLE `blogs` (
  `blog_id` int(8) NOT NULL,
  `title` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `author` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `author_id` int(8) NOT NULL,
  `likes_count` int(11) DEFAULT '0',
  `post_comment_id` int(8) DEFAULT '0',
  `image_url` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `blogs`
--

INSERT INTO `blogs` (`blog_id`, `title`, `content`, `author`, `created_at`, `author_id`, `likes_count`, `post_comment_id`, `image_url`) VALUES
(14, 'Sziasztok! #Első postom.', 'Ez az első bejegyés az oldalon. Remélem mindenkinek tetszik az oldal.\nHa felmerülne kérdés vagy valami visszajelzés fogalmazódna meg benned, akkor keresd a Contact us részt és írj egy üzenetet!\n\nSzép napot mindenkinek!', 'Admin', '2025-04-15 22:22:32', 7, 1, 0, 'blog_14_1744756356102.png'),
(52, 'Sziasztok! #Uppdate infó', 'Tudtátok, hogy az oldalon van már egyszerű DarkMode?\nHogy aktiválni tudjátok az oldal tetején a kis Hold ikonnal tudjátok megtenni.\n\nTovábbá ha felmerülne valami probléma az oldallal vagy csak írnátok nekünk azt a Contact us oldalon tudjátok megtenni!\nLegyen szép napotok!', 'Admin', '2025-04-17 17:07:13', 7, 1, 0, 'blog_52_1744909633909.png');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `cart`
--

CREATE TABLE `cart` (
  `cartId` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `createdAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `cart`
--

INSERT INTO `cart` (`cartId`, `userId`, `createdAt`, `updatedAt`, `status`) VALUES
(7, 1, '2025-04-23 17:56:56', '2025-04-23 19:52:57', 'OPEN'),
(8, 5, '2025-04-24 16:18:20', '2025-04-24 20:15:21', 'CLOSED'),
(9, 5, '2025-04-24 20:28:19', '2025-04-24 20:29:12', 'CLOSED'),
(10, 7, '2025-04-26 22:26:47', '2025-04-28 18:11:44', 'OPEN'),
(11, 23, '2025-04-28 12:39:24', '2025-04-28 18:13:58', 'CLOSED'),
(12, 23, '2025-04-28 18:17:32', '2025-04-28 18:18:09', 'CLOSED'),
(14, 23, '2025-04-28 18:36:39', '2025-04-28 18:36:59', 'CLOSED'),
(15, 23, '2025-04-28 18:43:23', '2025-04-28 19:11:32', 'CLOSED'),
(16, 23, '2025-04-28 19:43:15', '2025-04-28 19:43:21', 'CLOSED'),
(17, 23, '2025-04-28 19:43:27', '2025-04-28 19:43:27', 'OPEN');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `cart_items`
--

CREATE TABLE `cart_items` (
  `cartItem_id` int(11) NOT NULL,
  `cartId` int(11) NOT NULL,
  `productId` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `cart_items`
--

INSERT INTO `cart_items` (`cartItem_id`, `cartId`, `productId`, `quantity`, `unit_price`) VALUES
(4, 7, 2, 1, '46.00'),
(5, 7, 3, 1, '30.00'),
(6, 7, 5, 3, '20.00'),
(7, 8, 6, 3, '25.00'),
(8, 8, 4, 1, '113.00'),
(9, 9, 15, 1, '90.00'),
(10, 9, 10, 2, '60.00'),
(14, 11, 195, 1, '29000.00'),
(15, 11, 73, 1, '58000.00'),
(22, 12, 195, 1, '29000.00'),
(23, 12, 149, 1, '31000.00'),
(26, 14, 195, 2, '29000.00'),
(28, 15, 195, 1, '29000.00'),
(29, 16, 195, 2, '29000.00');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `categories`
--

CREATE TABLE `categories` (
  `category_id` int(8) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `categories`
--

INSERT INTO `categories` (`category_id`, `name`, `description`) VALUES
(1, 'Órák', 'Vintage órák és faliórák gyűjteménye.'),
(2, 'Ékszerek', 'Vintage stílusú gyűrűk, nyakláncok és karkötők.'),
(3, 'Gyertyatartók', 'Antik és retro gyertyatartók.'),
(4, 'Táskák', 'Kézzel készült vintage táskák.'),
(5, 'Lemezjátszók', 'Retro lemezjátszók és kiegészítők.'),
(6, 'Bakelit Lemez Gyűjtemények', 'Klasszikus zenék eredeti bakelit lemezeken.'),
(7, 'Lámpák', 'Vintage asztali és állólámpák.'),
(8, 'Falidíszek', 'Klasszikus faliképek és dísztárgyak.'),
(9, 'Bögrék', 'Retro bögrék és konyhai eszközök.'),
(10, 'Kamerák', 'Vintage kamerák és fotóeszközök.'),
(11, 'Játék Konzolok', 'Klasszikus retro játék konzolok.'),
(12, 'Játékok és Gyűjthető Darabok', 'Klasszikus játékok és különleges gyűjtői tárgyak.'),
(13, 'Porcelán', 'Régi porcelán tárgyak.'),
(14, 'Bőröndök', 'Retro bőröndök és utazótáskák.'),
(15, 'Evőeszközök', 'Antik és klasszikus evőeszköz szettek.'),
(16, 'Ruhafogasok', 'Vintage stílusú tároló és fogas rendszerek.'),
(17, 'Tükrök', 'Kovácsoltvas keretes tükrök.'),
(18, 'Könyvtartók', 'Retro és antik könyvtartók.');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `comments`
--

CREATE TABLE `comments` (
  `comment_id` int(11) NOT NULL,
  `blog_id` int(11) NOT NULL,
  `comment_text` text NOT NULL,
  `commenter` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `comments`
--

INSERT INTO `comments` (`comment_id`, `blog_id`, `comment_text`, `commenter`, `created_at`) VALUES
(1, 52, 'Így sötétben nem fogja a szemed zavarni az éles fény!', 'You', '2025-04-28 20:20:47');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `messages`
--

CREATE TABLE `messages` (
  `message_id` int(11) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `message` varchar(1000) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `email` varchar(255) NOT NULL,
  `Subject` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `messages`
--

INSERT INTO `messages` (`message_id`, `full_name`, `message`, `created_at`, `email`, `Subject`) VALUES
(2, 'John Doe', 'Hello, I have an issue', '2025-04-07 21:03:41', 'john@example.com', 'Inquiry'),
(9, 'Martin Gal', 'Itt mindenki tud Feedback-et írni. Nem kell hozzá hogy regisztrált legyen!', '2025-04-28 20:17:26', 'martingal2003@gmail.com', 'Test Feedback');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `newsletter_subscribers`
--

CREATE TABLE `newsletter_subscribers` (
  `subscriber_id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `newsletter_subscribers`
--

INSERT INTO `newsletter_subscribers` (`subscriber_id`, `email`, `full_name`, `created_at`) VALUES
(11, 'martingal2003@gmail.com', 'martingal2003', '2025-04-12 21:10:28'),
(13, 'gal.martin.ferenc@simonyiszki.org', 'gal.martin.ferenc', '2025-04-13 15:56:24');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `orders`
--

CREATE TABLE `orders` (
  `id` int(20) NOT NULL,
  `user_id` int(20) NOT NULL,
  `order_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) NOT NULL DEFAULT 'PENDING',
  `total` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `orders`
--

INSERT INTO `orders` (`id`, `user_id`, `order_date`, `status`, `total`) VALUES
(2, 5, '2025-04-24 22:15:21', 'PENDING', '138.00'),
(3, 5, '2025-04-24 22:29:12', 'PENDING', '210.00'),
(4, 23, '2025-04-28 20:13:58', 'PENDING', '87000.00'),
(5, 23, '2025-04-28 20:18:09', 'PENDING', '60012.00'),
(6, 23, '2025-04-28 20:33:54', 'PENDING', '58000.00'),
(7, 23, '2025-04-28 20:36:59', 'PENDING', '58000.00'),
(8, 23, '2025-04-28 21:11:31', 'PENDING', '29000.00'),
(9, 23, '2025-04-28 21:43:21', 'PENDING', '74660.00');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `order_items`
--

CREATE TABLE `order_items` (
  `order_item_id` int(8) NOT NULL,
  `order_id` int(8) NOT NULL,
  `product_id` int(8) NOT NULL,
  `quantity` int(11) NOT NULL,
  `unit_price` decimal(10,0) NOT NULL,
  `price` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `order_items`
--

INSERT INTO `order_items` (`order_item_id`, `order_id`, `product_id`, `quantity`, `unit_price`, `price`) VALUES
(1, 2, 6, 3, '25', '75.00'),
(2, 2, 4, 1, '113', '113.00'),
(3, 3, 15, 1, '90', '90.00'),
(4, 3, 10, 2, '60', '120.00'),
(5, 4, 195, 1, '36830', '29000.00'),
(6, 4, 73, 1, '73660', '58000.00'),
(7, 5, 195, 1, '36830', '29000.00'),
(8, 5, 149, 1, '39370', '31000.00'),
(9, 5, 233, 1, '15', '12.00'),
(10, 6, 195, 2, '36830', '58000.00'),
(11, 7, 195, 2, '36830', '58000.00'),
(12, 8, 195, 1, '36830', '29000.00'),
(13, 9, 195, 2, '36830', '73660.00');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `payments`
--

CREATE TABLE `payments` (
  `payment_id` int(8) NOT NULL,
  `order_id` int(8) NOT NULL,
  `payment_method` varchar(50) NOT NULL,
  `payment_date` datetime DEFAULT NULL,
  `amount` decimal(10,0) NOT NULL,
  `payment_status` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `products`
--

CREATE TABLE `products` (
  `product_id` int(8) NOT NULL,
  `name` varchar(255) NOT NULL,
  `slug` varchar(255) DEFAULT NULL,
  `meta_title` varchar(255) DEFAULT NULL,
  `description` text NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `discount_price` decimal(10,2) DEFAULT NULL,
  `stock_quanty` int(11) NOT NULL,
  `status` enum('active','inactive','archived','') NOT NULL DEFAULT 'active',
  `category_id` int(8) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` timestamp NULL DEFAULT NULL,
  `image_url` varchar(255) NOT NULL,
  `deleted_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `products`
--

INSERT INTO `products` (`product_id`, `name`, `slug`, `meta_title`, `description`, `price`, `discount_price`, `stock_quanty`, `status`, `category_id`, `created_at`, `updatedAt`, `image_url`, `deleted_at`) VALUES
(1, 'Cartier óra', 'cartier-ra', 'Vásárolható termék: Cartier óra', 'A világ első Cartier órája', '60.00', '0.00', 2, 'active', 1, '2025-04-14 11:34:03', '2025-04-22 20:30:39', '', NULL),
(2, 'Antik Fa Fali Óra', 'antik-fa-fali-ra', 'Vásárolható termék: Antik Fa Fali Óra', 'Klasszikus antik falióra kézi faragással.', '46.00', '0.00', 10, 'active', 1, '2025-04-14 11:34:03', '2025-04-15 09:44:57', '', NULL),
(3, 'Retro Zsebóra', 'retro-zseb-ra', 'Vásárolható termék: Retro Zsebóra', 'Ezüst zsebóra lánccal és gravírozott mintával.', '30.00', NULL, 15, 'active', 1, '2025-04-14 11:34:03', NULL, '', NULL),
(4, 'Aranyozott Asztali Óra', 'aranyozott-asztali-ra', 'Vásárolható termék: Aranyozott Asztali Óra', 'Elegáns aranyozott óra, viktoriánus stílusban.', '113.00', NULL, 2, 'active', 1, '2025-04-14 11:34:03', NULL, '', NULL),
(5, 'Vintage Gyöngysor', 'vintage-gyongysor', 'Vásárolható termék: Vintage Gyöngysor', 'Klasszikus gyöngysor kézzel készült díszítéssel.', '20.00', NULL, 20, 'active', 2, '2025-04-14 11:34:03', NULL, '', NULL),
(6, 'Ezüst Karkötő', 'ezust-karkotuo', 'Vásárolható termék: Ezüst Karkötő', 'Kézzel készített ezüst karkötő egyedi mintával.', '25.00', NULL, 12, 'active', 2, '2025-04-14 11:34:03', NULL, '', NULL),
(7, 'Antik Gyűrű', 'antik-gyuru', 'Vásárolható termék: Antik Gyűrű', 'Aranyozott gyűrű apró kövekkel díszítve.', '50.00', NULL, 1, 'active', 2, '2025-04-14 11:34:03', NULL, '', NULL),
(8, 'Kovácsoltvas Gyertyatartó', 'kovacsoltvas-gyertyatarto', 'Vásárolható termék: Kovácsoltvas Gyertyatartó', 'Egyedi, kézzel készült kovácsoltvas gyertyatartó.', '35.00', NULL, 10, 'active', 3, '2025-04-14 11:34:03', NULL, '', NULL),
(9, 'Bronz Asztali Gyertyatartó', 'bronz-asztali-gyertyatarto', 'Vásárolható termék: Bronz Asztali Gyertyatartó', 'Antik bronz gyertyatartó, 2 szál gyertyához.', '45.00', NULL, 7, 'active', 3, '2025-04-14 11:34:03', NULL, '', NULL),
(10, 'Kristály Gyertyatartó', 'kristaly-gyertyatarto', 'Vásárolható termék: Kristály Gyertyatartó', 'Elegáns kristály gyertyatartó díszdobozban.', '60.00', NULL, 5, 'active', 3, '2025-04-14 11:34:03', NULL, '', NULL),
(11, 'Bőr Oldaltáska', 'bor-oldaltaska', 'Vásárolható termék: Bőr Oldaltáska', 'Kézzel készült barna bőrtáska, vintage dizájnnal.', '80.00', NULL, 10, 'active', 4, '2025-04-14 11:34:03', NULL, '', NULL),
(12, 'Retro Vászontáska', 'retro-vaszontaska', 'Vásárolható termék: Retro Vászontáska', 'Nosztalgikus mintázatú vászontáska.', '20.00', NULL, 25, 'active', 4, '2025-04-14 11:34:03', NULL, '', NULL),
(13, 'Antik Kézitáska', 'antik-kezitaska', 'Vásárolható termék: Antik Kézitáska', 'Különleges, kézzel varrott kézitáska réz csatokkal.', '50.00', NULL, 7, 'active', 4, '2025-04-14 11:34:03', NULL, '', NULL),
(14, 'Retro Lemezjátszó', 'retro-lemezjatszo', 'Vásárolható termék: Retro Lemezjátszó', 'Klasszikus fa burkolatú lemezjátszó.', '160.00', NULL, 5, 'active', 5, '2025-04-14 11:34:03', NULL, '', NULL),
(15, 'Hordozható Lemezjátszó', 'hordozhato-lemezjatszo', 'Vásárolható termék: Hordozható Lemezjátszó', 'Vintage stílusú kompakt lemezjátszó.', '90.00', NULL, 8, 'active', 5, '2025-04-14 11:34:03', NULL, '', NULL),
(16, 'Hi-Fi Lemezjátszó', 'hi-fi-lemezjatszo', 'Vásárolható termék: Hi-Fi Lemezjátszó', 'Prémium minőségű retro lemezjátszó.', '200.00', NULL, 3, 'active', 5, '2025-04-14 11:34:03', NULL, '', NULL),
(17, 'Beatles Bakelit', 'beatles-bakelit', 'Vásárolható termék: Beatles Bakelit', 'Eredeti Beatles album bakeliten.', '40.00', '0.00', 15, 'active', 6, '2025-04-14 11:34:03', NULL, '/uploads/products/The_Beatles_Abbey_Road.png', NULL),
(18, 'Elvis Presley Bakelit', 'elvis-presley-bakelit', 'Vásárolható termék: Elvis Presley Bakelit', 'Elvis klasszikus dalai bakeliten.', '35.00', NULL, 12, 'active', 6, '2025-04-14 11:34:03', NULL, '', NULL),
(19, 'Queen Gyűjtemény', 'queen-gyujtemeny', 'Vásárolható termék: Queen Gyűjtemény', 'Queen legjobb dalai exkluzív bakeliten.', '50.00', NULL, 8, 'active', 6, '2025-04-14 11:34:03', NULL, '', NULL),
(20, 'Antik Asztali Lámpa', 'antik-asztali-lampa', 'Vásárolható termék: Antik Asztali Lámpa', 'Kézzel készített bronz asztali lámpa.', '80.00', NULL, 10, 'active', 7, '2025-04-14 11:34:03', NULL, '', NULL),
(21, 'Retro Állólámpa', 'retro-allolampa', 'Vásárolható termék: Retro Állólámpa', 'Klasszikus állólámpa vintage stílusban.', '130.00', NULL, 5, 'active', 7, '2025-04-14 11:34:03', NULL, '', NULL),
(22, 'Színes Üveglámpa', 'sines-uveglampa', 'Vásárolható termék: Színes Üveglámpa', 'Tiffany stílusú színes üveglámpa.', '90.00', NULL, 7, 'active', 7, '2025-04-14 11:34:03', NULL, '', NULL),
(23, 'Fém Fali Dekoráció', 'fem-fali-dekoracio', 'Vásárolható termék: Fém Fali Dekoráció', 'Kovácsoltvas fali dekoráció virágmintával.', '30.00', NULL, 12, 'active', 8, '2025-04-14 11:34:03', NULL, '', NULL),
(24, 'Retro Plakát', 'retro-plakat', 'Vásárolható termék: Retro Plakát', 'Vintage filmplakát fakeretben.', '20.00', NULL, 20, 'active', 8, '2025-04-14 11:34:03', NULL, '', NULL),
(25, 'Antik Tükör', 'antik-tukor', 'Vásárolható termék: Antik Tükör', 'Különleges kovácsoltvas keretű falitükör.', '80.00', NULL, 6, 'active', 8, '2025-04-14 11:34:03', NULL, '', NULL),
(26, 'Retro Bögre Szett', 'retro-bogre-szett', 'Vásárolható termék: Retro Bögre Szett', 'Klasszikus mintázatú kerámiabögrék szettje.', '25.00', NULL, 15, 'active', 9, '2025-04-14 11:34:03', NULL, '', NULL),
(27, 'Egyedi Nyomtatott Bögre', 'egyedi-nyomtattott-bogre', 'Vásárolható termék: Egyedi Nyomtatott Bögre', 'Kézzel festett retro bögre.', '15.00', NULL, 25, 'active', 9, '2025-04-14 11:34:03', NULL, '', NULL),
(28, 'Antik Porcelán Bögre', 'antik-porcelan-bogre', 'Vásárolható termék: Antik Porcelán Bögre', 'Elegáns, arany szélű porcelán bögre.', '20.00', NULL, 10, 'active', 9, '2025-04-14 11:34:03', NULL, '', NULL),
(29, 'Polaroid Kamera', 'polaroid-kamera', 'Vásárolható termék: Polaroid Kamera', 'Vintage Polaroid instant fényképezőgép.', '90.00', '0.00', 10, 'active', 9, '2025-04-14 11:34:03', '2025-04-14 15:24:02', '', NULL),
(30, 'Canon Filmkamera', 'canon-filmkamera', 'Vásárolható termék: Canon Filmkamera', 'Klasszikus Canon 35mm-es filmkamera.', '130.00', '0.00', 5, 'active', 9, '2025-04-14 11:34:03', '2025-04-14 15:23:18', '', NULL),
(31, 'Retro Videokamera', 'retro-videokamera', 'Vásárolható termék: Retro Videokamera', 'Antik videokamera gyűjtőknek.', '150.00', NULL, 3, 'active', 3, '2025-04-14 11:34:03', NULL, '', NULL),
(32, 'Atari 2600', 'atari-2600', 'Vásárolható termék: Atari 2600', 'Klasszikus Atari 2600 játékkonzol.', '100.00', NULL, 8, 'active', 11, '2025-04-14 11:34:03', NULL, '', NULL),
(33, 'Nintendo NES', 'nintendo-nes', 'Vásárolható termék: Nintendo NES', 'Eredeti Nintendo NES konzol.', '130.00', NULL, 6, 'active', 11, '2025-04-14 11:34:03', NULL, '', NULL),
(34, 'Sega Genesis', 'sega-genesis', 'Vásárolható termék: Sega Genesis', 'Retro Sega konzol gyűjtők számára.', '90.00', NULL, 10, 'active', 11, '2025-04-14 11:34:03', NULL, '', NULL),
(35, 'Monopoly Retro Kiadás', 'monopoly-retro-kiadas', 'Vásárolható termék: Monopoly Retro Kiadás', 'Klasszikus Monopoly társasjáték.', '50.00', NULL, 15, 'active', 12, '2025-04-14 11:34:03', NULL, '', NULL),
(36, 'Vintage Puzzle', 'vintage-puzzle', 'Vásárolható termék: Vintage Puzzle', '1000 darabos antik stílusú puzzle.', '20.00', NULL, 20, 'active', 12, '2025-04-14 11:34:03', NULL, '', NULL),
(37, 'Gyűjtői Akciófigura', 'gyujtoi-akciofigura', 'Vásárolható termék: Gyűjtői Akciófigura', 'Limitált kiadású vintage akciófigura.', '60.00', NULL, 5, 'active', 12, '2025-04-14 11:34:03', NULL, '', NULL),
(38, 'Antik Porcelán Váza', 'antik-porcelan-vaza', 'Vásárolható termék: Antik Porcelán Váza', 'Részletesen díszített antik váza.', '80.00', NULL, 7, 'active', 13, '2025-04-14 11:34:03', NULL, '', NULL),
(39, 'Porcelán Teás Készlet', 'porcelan-teas-keszlet', 'Vásárolható termék: Porcelán Teás Készlet', 'Klasszikus porcelán teáskészlet.', '100.00', NULL, 5, 'active', 13, '2025-04-14 11:34:03', NULL, '', NULL),
(40, 'Dekoratív Porcelán Tányér', 'dekorativ-porcelan-tanyer', 'Vásárolható termék: Dekoratív Porcelán Tányér', 'Elegáns, mintás porcelántányér.', '25.00', NULL, 12, 'active', 13, '2025-04-14 11:34:03', NULL, '', NULL),
(41, 'Antik Bőrönd', 'antik-borond', 'Vásárolható termék: Antik Bőrönd', 'Kézzel készített vintage bőrönd, régi stílusban.', '130.00', NULL, 5, 'active', 14, '2025-04-14 11:34:03', NULL, '', NULL),
(42, 'Retro Fa Utazóláda', 'retro-fa-utazolada', 'Vásárolható termék: Retro Fa Utazóláda', 'Egyedi, kézzel készített fa utazóláda.', '150.00', NULL, 3, 'active', 14, '2025-04-14 11:34:03', NULL, '', NULL),
(43, 'Vintage Kézipoggyász', 'vintage-kezipoggyasz', 'Vásárolható termék: Vintage Kézipoggyász', 'Klasszikus stílusú kézipoggyász.', '90.00', NULL, 8, 'active', 14, '2025-04-14 11:34:03', NULL, '', NULL),
(44, 'Ezüst Evőeszköz Készlet', 'ezust-evoszkoz-keszlet', 'Vásárolható termék: Ezüst Evőeszköz Készlet', 'Klasszikus ezüst evőeszközök.', '60.00', NULL, 10, 'active', 15, '2025-04-14 11:34:03', NULL, '', NULL),
(45, 'Réz Kiskanál Szett', 'rez-kiskanal-szett', 'Vásárolható termék: Réz Kiskanál Szett', 'Egyedi réz kiskanalak vintage stílusban.', '20.00', NULL, 20, 'active', 15, '2025-04-14 11:34:03', NULL, '', NULL),
(46, 'Antik Evőeszköz Szett', 'antik-evoseszkoz-szett', 'Vásárolható termék: Antik Evőeszköz Szett', 'Exkluzív antik evőeszköz készlet.', '100.00', NULL, 5, 'active', 15, '2025-04-14 11:34:03', NULL, '', NULL),
(47, 'Kovácsoltvas Fogas', 'kovacsoltvas-fogas', 'Vásárolható termék: Kovácsoltvas Fogas', 'Elegáns falra szerelhető kovácsoltvas fogas.', '35.00', NULL, 12, 'active', 16, '2025-04-14 11:34:03', NULL, '', NULL),
(48, 'Vintage Fa Fogas', 'vintage-fa-fogas', 'Vásárolható termék: Vintage Fa Fogas', 'Kézzel készített fa ruhafogas.', '25.00', NULL, 15, 'active', 16, '2025-04-14 11:34:03', NULL, '', NULL),
(49, 'Antik Álló Fogas', 'antik-allo-fogas', 'Vásárolható termék: Antik Álló Fogas', 'Klasszikus antik stílusú álló fogas.', '80.00', NULL, 5, 'active', 16, '2025-04-14 11:34:03', NULL, '', NULL),
(50, 'Kör alakú Kovácsoltvas Tükör', 'kor-alaku-kovacsoltvas-tukor', 'Vásárolható termék: Kör alakú Kovácsoltvas Tükör', 'Kézzel készített kör alakú antik tükör.', '90.00', NULL, 7, 'active', 17, '2025-04-14 11:34:03', NULL, '', NULL),
(51, 'Díszített Falitükör', 'diszitett-falitukor', 'Vásárolható termék: Díszített Falitükör', 'Vintage díszített keretű falitükör.', '100.00', NULL, 5, 'active', 17, '2025-04-14 11:34:03', NULL, '', NULL),
(52, 'Álló Antik Tükör', 'allo-antik-tukor', 'Vásárolható termék: Álló Antik Tükör', 'Nagyméretű álló antik tükör.', '150.00', NULL, 3, 'active', 17, '2025-04-14 11:34:03', NULL, '', NULL),
(53, 'Vintage Seiko férfi karóra', 'vintage-seiko-ferfi-korora', 'Vásárolható termék: Vintage Seiko férfi karóra', '1970-es évekbeli japán Seiko karóra, rozsdamentes acél tokkal.', '45000.00', NULL, 4, 'active', 1, '2025-04-14 11:34:03', NULL, '', NULL),
(54, 'Omega De Ville klasszikus óra', 'omega-de-ville-klasszikus-ora', 'Vásárolható termék: Omega De Ville klasszikus óra', 'Elegáns, kézzel felhúzható mechanikus Omega óra bőrszíjjal.', '125000.00', NULL, 2, 'active', 1, '2025-04-14 11:34:03', NULL, '', NULL),
(55, 'Zsebóra lánccal - antik stílus', 'zsebora-lanccal-antik-stilus', 'Vásárolható termék: Zsebóra lánccal - antik stílus', 'Mechanikus zsebóra díszes gravírozással, lánccal együtt.', '28500.00', NULL, 6, 'active', 1, '2025-04-14 11:34:03', NULL, '', NULL),
(56, 'Cartier Tank replika', 'cartier-tank-replika', 'Vásárolható termék: Cartier Tank replika', 'Vintage megjelenésű Cartier Tank stílusú óra aranyszínű tokkal.', '39000.00', NULL, 5, 'active', 1, '2025-04-14 11:34:03', NULL, 'http://localhost:8080/uploads/products/Cartier_Tank_replika.png', NULL),
(57, 'Szovjet Raketa karóra', 'szovjet-raketa-korora', 'Vásárolható termék: Szovjet Raketa karóra', 'Retro orosz Raketa karóra piros számlappal, gyűjtőknek ajánlott.', '32000.00', NULL, 3, 'active', 1, '2025-04-14 11:34:03', NULL, '', NULL),
(58, 'Antik falióra faragott fával', 'antik-faliora-faragott-faval', 'Vásárolható termék: Antik falióra faragott fával', 'Nagy méretű, faragott fából készült falióra ingával.', '65000.00', NULL, 2, 'active', 1, '2025-04-14 11:34:03', NULL, '', NULL),
(59, 'Casio digitális óra - 1980', 'casio-digitalis-ora-1980', 'Vásárolható termék: Casio digitális óra - 1980', 'Eredeti Casio digitális óra az 1980-as évekből.', '18000.00', NULL, 10, 'active', 1, '2025-04-14 11:34:03', NULL, '', NULL),
(60, 'Fali óra római számokkal', 'fali-ora-romai-számokkal', 'Vásárolható termék: Fali óra római számokkal', 'Vintage stílusú falióra római számos számlappal.', '23000.00', NULL, 7, 'active', 1, '2025-04-14 11:34:03', NULL, '', NULL),
(61, 'Junghans mechanikus óra', 'junghans-mechanikus-ora', 'Vásárolható termék: Junghans mechanikus óra', 'Német gyártású klasszikus Junghans óra automata szerkezettel.', '79000.00', NULL, 3, 'active', 1, '2025-04-14 11:34:03', NULL, '', NULL),
(62, 'Swatch 1995 limitált kiadás', 'swatch-1995-limitalt-kiadas', 'Vásárolható termék: Swatch 1995 limitált kiadás', 'Színes Swatch karóra 1995-ből, gyűjtői darab.', '27000.00', NULL, 8, 'active', 1, '2025-04-14 11:34:03', NULL, '', NULL),
(63, 'Aranyozott vintage nyaklánc', 'aranyozott-vintage-nyaklanc', 'Vásárolható termék: Aranyozott vintage nyaklánc', 'Kézzel készített aranyozott nyaklánc, klasszikus dizájnnal.', '42000.00', NULL, 5, 'active', 2, '2025-04-14 11:34:03', NULL, '', NULL),
(64, 'Ezüst karkötő - 925 sterling', 'ezust-karkotuo-925-sterling', 'Vásárolható termék: Ezüst karkötő - 925 sterling', 'Finom kidolgozású ezüst karkötő, valódi sterling jelzéssel.', '37000.00', NULL, 6, 'active', 2, '2025-04-14 11:34:03', NULL, '', NULL),
(65, 'Gyöngy fülbevaló', 'gyongy-fulbevalo', 'Vásárolható termék: Gyöngy fülbevaló', 'Klasszikus, kézzel készített gyöngy fülbevaló.', '25000.00', NULL, 8, 'active', 2, '2025-04-14 11:34:03', NULL, '', NULL),
(66, 'Vintage bross - virágos motívum', 'vintage-bross-viragos-motivum', 'Vásárolható termék: Vintage bross - virágos motívum', 'Régi stílusú bross színes kövekkel és virágdísszel.', '18000.00', NULL, 10, 'active', 2, '2025-04-14 11:34:03', NULL, '', NULL),
(67, 'Zománcos art deco nyaklánc', 'zomancos-art-deco-nyaklanc', 'Vásárolható termék: Zománcos art deco nyaklánc', '1930-as évek stílusában zománcos medállal.', '34000.00', NULL, 4, 'active', 2, '2025-04-14 11:34:03', NULL, '', NULL),
(68, 'Platina eljegyzési gyűrű', 'platina-eljegyzesi-gyuru', 'Vásárolható termék: Platina eljegyzési gyűrű', 'Prémium platina gyűrű gyémánt utánzattal.', '75000.00', NULL, 2, 'active', 2, '2025-04-14 11:34:03', NULL, '', NULL),
(69, 'Antik ezüst medál', 'antik-ezust-medal', 'Vásárolható termék: Antik ezüst medál', 'Különleges gravírozott ezüst medál, vésett mintával.', '29500.00', NULL, 5, 'active', 2, '2025-04-14 11:34:03', NULL, '', NULL),
(70, 'Színes kristályos nyaklánc', 'sines-kristalyos-nyaklanc', 'Vásárolható termék: Színes kristályos nyaklánc', 'Többsoros lánc csillogó kristály díszekkel.', '31000.00', NULL, 6, 'active', 2, '2025-04-14 11:34:03', NULL, '', NULL),
(71, 'Retro gyűrű - türkiz kővel', 'retro-gyuru-turkiz-kovel', 'Vásárolható termék: Retro gyűrű - türkiz kővel', 'Nagy méretű, feltűnő gyűrű türkiz kő díszítéssel.', '27000.00', NULL, 7, 'active', 2, '2025-04-14 11:34:03', NULL, '', NULL),
(72, 'Szív alakú medál nyaklánccal', 'siv-alaku-medal-nyaklancsal', 'Vásárolható termék: Szív alakú medál nyaklánccal', 'Érzelmes vintage medál, díszdobozos kiszerelésben.', '22000.00', NULL, 9, 'active', 2, '2025-04-14 11:34:03', NULL, '', NULL),
(73, 'Antik bronz gyertyatartó', 'antik-bronz-gyertyatarto', 'Vásárolható termék: Antik bronz gyertyatartó', 'Masszív, kézzel faragott bronz gyertyatartó a XIX. századból.', '58000.00', NULL, 3, 'active', 3, '2025-04-14 11:34:03', NULL, '', NULL),
(74, 'Kovácsoltvas falra szerelhető tartó', 'kovacsoltvas-falra-szerelheto-tarto', 'Vásárolható termék: Kovácsoltvas falra szerelhető tartó', 'Díszes fali gyertyatartó rusztikus stílusban.', '31000.00', NULL, 5, 'active', 3, '2025-04-14 11:34:03', NULL, '', NULL),
(75, 'Kristály gyertyatartó pár', 'kristaly-gyertyatarto-par', 'Vásárolható termék: Kristály gyertyatartó pár', 'Két darabos elegáns kristály szett ünnepi asztalra.', '47000.00', NULL, 4, 'active', 3, '2025-04-14 11:34:03', NULL, '', NULL),
(76, 'Barokk stílusú gyertyatartó', 'barokk-stilusú-gyertyatarto', 'Vásárolható termék: Barokk stílusú gyertyatartó', 'Gazdagon díszített, aranyszínű antik hatású darab.', '39500.00', NULL, 6, 'active', 3, '2025-04-14 11:34:03', NULL, '', NULL),
(77, 'Skandináv vintage fa tartó', 'skandinav-vintage-fa-tarto', 'Vásárolható termék: Skandináv vintage fa tartó', 'Minimalista fa gyertyatartó északi stílusban.', '21500.00', NULL, 7, 'active', 3, '2025-04-14 11:34:03', NULL, '', NULL),
(78, 'Üveg gyertyatartó 1930-ból', 'uveg-gyertyatarto-1930-bol', 'Vásárolható termék: Üveg gyertyatartó 1930-ból', 'Art deco stílusú metszett üveg tartó, gyűjtői darab.', '36000.00', NULL, 4, 'active', 3, '2025-04-14 11:34:03', NULL, '', NULL),
(79, 'Viktoriánus stílusú gyertyatartó', 'viktorianus-stilusú-gyertyatarto', 'Vásárolható termék: Viktoriánus stílusú gyertyatartó', 'Finoman kidolgozott réz darab, angol stílusban.', '44000.00', NULL, 3, 'active', 3, '2025-04-14 11:34:03', NULL, '', NULL),
(80, 'Porcelán virágos gyertyatartó', 'porcelan-viragos-gyertyatarto', 'Vásárolható termék: Porcelán virágos gyertyatartó', 'Festett porcelán gyertyatartó virágdíszítéssel.', '29500.00', NULL, 6, 'active', 3, '2025-04-14 11:34:03', NULL, '', NULL),
(81, 'Retro háromágú gyertyatartó', 'retro-haromagu-gyertyatarto', 'Vásárolható termék: Retro háromágú gyertyatartó', 'Fekete fém test, klasszikus 60-as évekbeli formavilág.', '33000.00', NULL, 5, 'active', 3, '2025-04-14 11:34:03', NULL, '', NULL),
(82, 'Álló kandalló gyertyatartó', 'allo-kandallo-gyertyatarto', 'Vásárolható termék: Álló kandalló gyertyatartó', 'Nagy méretű, elegáns álló gyertyatartó kandalló elé.', '67000.00', NULL, 2, 'active', 3, '2025-04-14 11:34:03', NULL, '', NULL),
(83, 'Vintage bőr válltáska', 'vintage-bor-valltaska', 'Vásárolható termék: Vintage bőr válltáska', 'Kézzel varrott, valódi bőrből készült klasszikus válltáska.', '52000.00', NULL, 4, 'active', 4, '2025-04-14 11:34:03', NULL, '', NULL),
(84, 'Retro kézitáska', 'retro-kezitaska', 'Vásárolható termék: Retro kézitáska', '1950-es évekbeli elegáns kézitáska fém csattal.', '39000.00', NULL, 6, 'active', 4, '2025-04-14 11:34:03', NULL, '', NULL),
(85, 'Textil városi hátizsák', 'textil-varosi-hatizsak', 'Vásárolható termék: Textil városi hátizsák', 'Nosztalgikus mintázatú, vízlepergető textil hátizsák.', '27500.00', NULL, 5, 'active', 4, '2025-04-14 11:34:03', NULL, '', NULL),
(86, 'Patchwork oldaltáska', 'patchwork-oldaltaska', 'Vásárolható termék: Patchwork oldaltáska', 'Kézzel készült színes patchwork mintás vászontáska.', '18000.00', NULL, 8, 'active', 4, '2025-04-14 11:34:03', NULL, '', NULL),
(87, 'Klasszikus aktatáska', 'klasszikus-aktataska', 'Vásárolható termék: Klasszikus aktatáska', 'Fekete vintage aktatáska fémzárral és bőrből.', '45000.00', NULL, 3, 'active', 4, '2025-04-14 11:34:03', NULL, '', NULL),
(88, 'Mini bőrtáska - kézzel festett', 'mini-bortaska-kezelt-festett', 'Vásárolható termék: Mini bőrtáska - kézzel festett', 'Kézzel festett virágmintás kisméretű vállpántos táska.', '32000.00', NULL, 4, 'active', 4, '2025-04-14 11:34:03', NULL, '', NULL),
(89, 'Bélelt válltáska - kockás mintával', 'belelt-valltaska-kockas-mintaval', 'Vásárolható termék: Bélelt válltáska - kockás mintával', 'Puhán párnázott retró stílusú táska 1970-ből.', '26000.00', NULL, 7, 'active', 4, '2025-04-14 11:34:03', NULL, '', NULL),
(90, 'Tengerészkék vászon oldaltáska', 'tengereszke-vason-oldaltaska', 'Vásárolható termék: Tengerészkék vászon oldaltáska', 'Időtálló vintage táska sárgaréz csatokkal.', '24000.00', NULL, 6, 'active', 4, '2025-04-14 11:34:03', NULL, '', NULL),
(91, 'Retro ridikül - gyöngyház gombbal', 'retro-ridikul-gyongyhaz-gombbal', 'Vásárolható termék: Retro ridikül - gyöngyház gombbal', 'Kézzel készített klasszikus női táska.', '29500.00', NULL, 5, 'active', 4, '2025-04-14 11:34:03', NULL, '', NULL),
(92, 'Bőrből készült hátitáska', 'borbol-keszult-hatitaska', 'Vásárolható termék: Bőrből készült hátitáska', 'Eredeti retro túratáska valódi bőrből.', '48000.00', NULL, 3, 'active', 4, '2025-04-14 11:34:03', NULL, '', NULL),
(93, 'Audio-Technica AT-LP120X', 'audio-technica-at-lp120x', 'Vásárolható termék: Audio-Technica AT-LP120X', 'Professzionális bakelit lejátszó, precíz tűkar és USB digitalizálás.', '110000.00', NULL, 6, 'active', 5, '2025-04-14 11:34:03', NULL, '', NULL),
(94, 'Sony PS-LX310BT', 'sony-ps-lx310bt', 'Vásárolható termék: Sony PS-LX310BT', 'Bluetooth kompatibilis, automatikus működésű lemezjátszó.', '85000.00', NULL, 8, 'active', 5, '2025-04-14 11:34:03', NULL, '', NULL),
(95, 'Technics SL-1210MK7', 'technics-sl-1210mk7', 'Vásárolható termék: Technics SL-1210MK7', 'Legendás DJ lemezjátszó, masszív kivitel és kiváló hangminőség.', '160000.00', NULL, 4, 'active', 5, '2025-04-14 11:34:03', NULL, '', NULL),
(96, 'Pro-Ject Debut Carbon Evo', 'pro-ject-debut-carbon-evo', 'Vásárolható termék: Pro-Ject Debut Carbon Evo', 'Karbon karos prémium lejátszó csendes szíjhajtással.', '130000.00', NULL, 5, 'active', 5, '2025-04-14 11:34:03', NULL, '', NULL),
(97, 'Rega Planar 3', 'rega-planar-3', 'Vásárolható termék: Rega Planar 3', 'Audiophile kategóriás kézi hajtású lejátszó.', '140000.00', NULL, 4, 'active', 5, '2025-04-14 11:34:03', NULL, '', NULL),
(98, 'Victrola 8-in-1', 'victrola-8-in-1', 'Vásárolható termék: Victrola 8-in-1', 'Többfunkciós vintage lejátszó: CD, rádió, USB, kazetta.', '75000.00', NULL, 7, 'active', 5, '2025-04-14 11:34:03', NULL, '', NULL),
(99, 'Denon DP-300F', 'denon-dp-300f', 'Vásárolható termék: Denon DP-300F', 'Automata működés, kiváló hangzás, elegáns dizájn.', '90000.00', NULL, 9, 'active', 5, '2025-04-14 11:34:03', NULL, '', NULL),
(100, 'Crosley Cruiser Deluxe', 'crosley-cruiser-deluxe', 'Vásárolható termék: Crosley Cruiser Deluxe', 'Hordozható vintage stílusú lejátszó, beépített hangszóróval.', '45000.00', NULL, 10, 'active', 5, '2025-04-14 11:34:03', NULL, '', NULL),
(101, 'Lenco L-85', 'lenco-l-85', 'Vásárolható termék: Lenco L-85', 'USB digitalizálás, beépített előerősítő, retró stílus.', '62000.00', NULL, 8, 'active', 5, '2025-04-14 11:34:03', NULL, '', NULL),
(102, 'Fluance RT81', 'fluance-rt81', 'Vásárolható termék: Fluance RT81', 'Masszív fa ház, prémium alkatrészek, kiváló hangzás.', '98000.00', NULL, 5, 'active', 5, '2025-04-14 11:34:03', NULL, '', NULL),
(103, 'The Beatles - Abbey Road', 'the-beatles-abbey-road', 'Vásárolható termék: The Beatles - Abbey Road', 'Legendás rockalbum 1969-ből, eredeti bakelit kiadás.', '12500.00', NULL, 15, 'active', 6, '2025-04-14 11:34:03', NULL, '', NULL),
(104, 'Pink Floyd - The Dark Side of the Moon', 'pink-floyd-the-dark-side-of-the-moon', 'Vásárolható termék: Pink Floyd - The Dark Side of the Moon', 'Klasszikus progresszív rock album különleges borítóval.', '13500.00', NULL, 10, 'active', 6, '2025-04-14 11:34:03', NULL, '', NULL),
(105, 'Led Zeppelin IV', 'led-zeppelin-iv', 'Vásárolható termék: Led Zeppelin IV', 'Az egyik legmeghatározóbb rocklemez, 1971.', '12800.00', NULL, 12, 'active', 6, '2025-04-14 11:34:03', NULL, '', NULL),
(106, 'Queen - A Night at the Opera', 'queen-a-night-at-the-opera', 'Vásárolható termék: Queen - A Night at the Opera', 'Freddie Mercury ikonikus albuma, remek hangminőséggel.', '14000.00', NULL, 8, 'active', 6, '2025-04-14 11:34:03', NULL, '', NULL),
(107, 'David Bowie - Ziggy Stardust', 'david-bowie-ziggy-stardust', 'Vásárolható termék: David Bowie - Ziggy Stardust', 'Stílusteremtő glam rock album Bowie-tól.', '13200.00', NULL, 9, 'active', 6, '2025-04-14 11:34:03', NULL, '', NULL),
(108, 'Michael Jackson - Thriller', 'michael-jackson-thriller', 'Vásárolható termék: Michael Jackson - Thriller', 'A világ legsikeresebb albuma, eredeti nyomat.', '14500.00', NULL, 7, 'active', 6, '2025-04-14 11:34:03', NULL, '', NULL),
(109, 'Nirvana - Nevermind', 'nirvana-nevermind', 'Vásárolható termék: Nirvana - Nevermind', 'Alternatív rock klasszikus, kultikus borítóval.', '12000.00', NULL, 14, 'active', 6, '2025-04-14 11:34:03', NULL, '', NULL),
(110, 'The Rolling Stones - Sticky Fingers', 'the-rolling-stones-sticky-fingers', 'Vásárolható termék: The Rolling Stones - Sticky Fingers', 'Rock and roll klasszikus Andy Warhol borítóval.', '12500.00', NULL, 10, 'active', 6, '2025-04-14 11:34:03', NULL, '', NULL),
(111, 'Metallica - Master of Puppets', 'metallica-master-of-puppets', 'Vásárolható termék: Metallica - Master of Puppets', 'Thrash metal mestermű, 1986-os kiadás.', '13000.00', NULL, 11, 'active', 6, '2025-04-14 11:34:03', NULL, '', NULL),
(112, 'Radiohead - OK Computer', 'radiohead-ok-computer', 'Vásárolható termék: Radiohead - OK Computer', 'Alternatív rock alapmű 1997-ből.', '12700.00', NULL, 13, 'active', 6, '2025-04-14 11:34:03', NULL, '', NULL),
(113, 'Modern Állólámpa', 'modern-allolampa', 'Vásárolható termék: Modern Állólámpa', 'Minimalista stílusú LED állólámpa.', '45000.00', NULL, 10, 'active', 7, '2025-04-14 11:34:03', NULL, '', NULL),
(114, 'Asztali Olvasólámpa', 'asztali-olvasolampa', 'Vásárolható termék: Asztali Olvasólámpa', 'Állítható karú lámpa meleg fényű LED izzóval.', '22000.00', NULL, 15, 'active', 7, '2025-04-14 11:34:03', NULL, '', NULL),
(115, 'Kristály Csillár', 'kristaly-csillar', 'Vásárolható termék: Kristály Csillár', 'Többszintes kristálycsillár klasszikus eleganciával.', '150000.00', NULL, 5, 'active', 7, '2025-04-14 11:34:03', NULL, '', NULL),
(116, 'LED Mennyezeti Lámpa', 'led-mennyezeti-lampa', 'Vásárolható termék: LED Mennyezeti Lámpa', 'Távirányítós fényerőszabályzással és színhőmérséklet-állítással.', '60000.00', NULL, 12, 'active', 7, '2025-04-14 11:34:03', NULL, '', NULL),
(117, 'Indusztriális Fali Lámpa', 'indusztrialis-fali-lampa', 'Vásárolható termék: Indusztriális Fali Lámpa', 'Loft stílusú fém lámpa Edison izzóval.', '38000.00', NULL, 8, 'active', 7, '2025-04-14 11:34:03', NULL, '', NULL),
(118, 'Napenergia Lámpa', 'napenergia-lampa', 'Vásárolható termék: Napenergia Lámpa', 'Kültéri napelemes lámpa mozgásérzékelővel.', '25000.00', NULL, 20, 'active', 7, '2025-04-14 11:34:03', NULL, '', NULL),
(119, 'Vintage Asztali Lámpa', 'vintage-asztali-lampa', 'Vásárolható termék: Vintage Asztali Lámpa', 'Réz kivitelű, patinás hangulatvilágítás.', '42000.00', NULL, 6, 'active', 7, '2025-04-14 11:34:03', NULL, '', NULL),
(120, 'Éjszakai Fény Projektor', 'ejskanai-feny-projektor', 'Vásárolható termék: Éjszakai Fény Projektor', 'Csillagos égbolt mintázatú projektoros lámpa.', '33000.00', NULL, 14, 'active', 7, '2025-04-14 11:34:03', NULL, '', NULL),
(121, 'RGB Gamer Lámpa', 'rgb-gamer-lampa', 'Vásárolható termék: RGB Gamer Lámpa', 'Színes LED világítás, USB-s tápellátás, gamer dizájn.', '48000.00', NULL, 10, 'active', 7, '2025-04-14 11:34:03', NULL, '', NULL),
(122, 'Japán Stílusú Papírlámpa', 'japan-stilus-ru-papirlampa', 'Vásárolható termék: Japán Stílusú Papírlámpa', 'Természetes hatású papírbúra fa vázzal.', '27000.00', NULL, 18, 'active', 7, '2025-04-14 11:34:03', NULL, '', NULL),
(123, 'Vintage Fém Falidísz', 'vintage-fem-falidisz', 'Vásárolható termék: Vintage Fém Falidísz', 'Kovácsoltvasból készült, ornamentikus mintázatú falidísz.', '32000.00', NULL, 6, 'active', 8, '2025-04-14 11:34:03', NULL, '', NULL),
(124, 'Fa Keretes Fali Tükör', 'fa-keretes-fali-tukor', 'Vásárolható termék: Fa Keretes Fali Tükör', 'Kézzel faragott fa kerettel ellátott vintage tükör.', '46000.00', NULL, 4, 'active', 8, '2025-04-14 11:34:03', NULL, '', NULL),
(125, 'Macskás Textil Falikép', 'macskas-textil-falikep', 'Vásárolható termék: Macskás Textil Falikép', 'Puha textilből készült falikép aranyos mintával.', '17500.00', NULL, 10, 'active', 8, '2025-04-14 11:34:03', NULL, '', NULL),
(126, 'Kézzel Festett Falilap', 'kezellel-festett-falilap', 'Vásárolható termék: Kézzel Festett Falilap', 'Kerámia alapú, kézzel festett mediterrán stílusú dísz.', '29500.00', NULL, 5, 'active', 8, '2025-04-14 11:34:03', NULL, '', NULL),
(127, 'Bordás Fa Dekorpanel', 'bordas-fa-dekorpanel', 'Vásárolható termék: Bordás Fa Dekorpanel', 'Skandináv stílusú fa dekorációs elem fali rögzítéssel.', '28000.00', NULL, 7, 'active', 8, '2025-04-14 11:34:03', NULL, '', NULL),
(128, 'Retro Neon Fény Dekor', 'retro-neon-feny-dekor', 'Vásárolható termék: Retro Neon Fény Dekor', 'Rózsaszín neon világítású dekor felirat falra.', '34000.00', NULL, 3, 'active', 8, '2025-04-14 11:34:03', NULL, '', NULL),
(129, 'Antik Réz Falitányér', 'antik-rez-falitanyer', 'Vásárolható termék: Antik Réz Falitányér', 'Dombornyomott, falra akasztható réz dísztányér.', '23000.00', NULL, 6, 'active', 8, '2025-04-14 11:34:03', NULL, '', NULL),
(130, 'Papírvirágos Falidísz', 'papirviragos-falidisz', 'Vásárolható termék: Papírvirágos Falidísz', 'Színes papírvirágokból készült kézműves fali dekoráció.', '15000.00', NULL, 12, 'active', 8, '2025-04-14 11:34:03', NULL, '', NULL),
(131, 'Buddha Fali Dombormű', 'buddha-fali-dombormu', 'Vásárolható termék: Buddha Fali Dombormű', 'Fából faragott spirituális témájú dombormű.', '39000.00', NULL, 4, 'active', 8, '2025-04-14 11:34:03', NULL, '', NULL),
(132, 'Falra Szerelhető Polcdekor', 'falra-szerelheto-polcdekor', 'Vásárolható termék: Falra Szerelhető Polcdekor', 'Minimalista lebegő polc mintás háttérrel.', '25000.00', NULL, 8, 'active', 8, '2025-04-14 11:34:03', NULL, '', NULL),
(133, 'Bögre - Macskás design', 'bogre-macskas-design', 'Vásárolható termék: Bögre - Macskás design', 'Kerámia bögre macskás mintával, kézzel festett.', '4500.00', NULL, 30, 'active', 9, '2025-04-14 11:34:03', NULL, '', NULL),
(134, 'Vintage Porcelán Bögre', 'vintage-porcelan-bogre', 'Vásárolható termék: Vintage Porcelán Bögre', 'Klasszikus mintázatú, prémium porcelánból készült bögre.', '8900.00', NULL, 12, 'active', 9, '2025-04-14 11:34:03', NULL, '', NULL),
(135, 'Termo Bögre', 'termo-bogre', 'Vásárolható termék: Termo Bögre', 'Hőtartó, fedeles bögre rozsdamentes acél belsővel.', '6500.00', NULL, 15, 'active', 9, '2025-04-14 11:34:03', NULL, '', NULL),
(136, 'Vicces Feliratos Bögre', 'vicces-feliratos-bogre', 'Vásárolható termék: Vicces Feliratos Bögre', 'Szórakoztató szöveggel ellátott ajándékbögre.', '3500.00', NULL, 50, 'active', 9, '2025-04-14 11:34:03', NULL, '', NULL),
(137, 'Színes Zománcozott Bögre', 'sines-zomancozott-bogre', 'Vásárolható termék: Színes Zománcozott Bögre', 'Retro stílusú, könnyű zománcozott fém bögre.', '5700.00', NULL, 20, 'active', 9, '2025-04-14 11:34:03', NULL, '', NULL),
(138, 'Nagyméretű Kávés Bögre', 'nagymeretu-kaves-bogre', 'Vásárolható termék: Nagyméretű Kávés Bögre', 'Extra nagy kapacitású bögre kényelmes fogantyúval.', '7500.00', NULL, 18, 'active', 9, '2025-04-14 11:34:03', NULL, '', NULL),
(139, 'Elegáns Üveg Bögre', 'elegans-uveg-bogre', 'Vásárolható termék: Elegáns Üveg Bögre', 'Minimalista design, hőálló üvegből készült.', '9900.00', NULL, 10, 'active', 9, '2025-04-14 11:34:03', NULL, '', NULL),
(140, 'Fekete Matt Bögre', 'fekete-matt-bogre', 'Vásárolható termék: Fekete Matt Bögre', 'Modern stílusú, strapabíró kerámiabögre matt bevonattal.', '5200.00', NULL, 22, 'active', 9, '2025-04-14 11:34:03', NULL, '', NULL),
(141, 'Kerámia Latte Bögre', 'keramia-latte-bogre', 'Vásárolható termék: Kerámia Latte Bögre', 'Hosszúkás forma, barista stílusú latte bögre.', '6800.00', NULL, 25, 'active', 9, '2025-04-14 11:34:03', NULL, '', NULL),
(142, 'Művészi Mintás Bögre', 'muveszi-mintas-bogre', 'Vásárolható termék: Művészi Mintás Bögre', 'Egyedi festésű, kézzel készített művészi bögre.', '7200.00', NULL, 14, 'active', 9, '2025-04-14 11:34:03', NULL, '', NULL),
(143, 'Polaroid Land Camera 1000', 'polaroid-land-camera-1000', 'Vásárolható termék: Polaroid Land Camera 1000', 'Azonnali képnyomtatós retro fényképezőgép, szivárvány csíkkal.', '39000.00', NULL, 5, 'active', 10, '2025-04-14 11:34:03', NULL, '', NULL),
(144, 'Zenit-E Szovjet Kamera', 'zenit-e-szovjet-kamera', 'Vásárolható termék: Zenit-E Szovjet Kamera', 'Klasszikus filmes tükörreflexes fényképezőgép Helios objektívvel.', '45000.00', NULL, 3, 'active', 10, '2025-04-14 11:34:03', NULL, '', NULL),
(145, 'Canon AE-1 Program', 'canon-ae-1-program', 'Vásárolható termék: Canon AE-1 Program', '1980-as évekbeli filmes SLR, kiváló kezdőknek.', '62000.00', NULL, 4, 'active', 10, '2025-04-14 11:34:03', NULL, '', NULL),
(146, 'Kodak Brownie Flash II', 'kodak-brownie-flash-ii', 'Vásárolható termék: Kodak Brownie Flash II', 'Vintage boxkamera, gyűjtői darab.', '28000.00', NULL, 6, 'active', 10, '2025-04-14 11:34:03', NULL, '', NULL),
(147, 'Olympus Trip 35', 'olympus-trip-35', 'Vásárolható termék: Olympus Trip 35', 'Kompakt filmes kamera, letisztult dizájn, fix objektívvel.', '51000.00', NULL, 5, 'active', 10, '2025-04-14 11:34:03', NULL, '', NULL),
(148, 'Minolta X-700', 'minolta-x-700', 'Vásárolható termék: Minolta X-700', 'SLR gép automatikus és manuális móddal, filmes fotózáshoz.', '58000.00', NULL, 4, 'active', 10, '2025-04-14 11:34:03', NULL, '', NULL),
(149, 'Agfa Clack', 'agfa-clack', 'Vásárolható termék: Agfa Clack', 'Német boxkamera 1950-es évekből, középformátumú filmre.', '31000.00', NULL, 3, 'active', 10, '2025-04-14 11:34:03', NULL, '', NULL),
(150, 'Leica M3 replika', 'leica-m3-replika', 'Vásárolható termék: Leica M3 replika', 'Klasszikus Leica stílusú filmes gép, makulátlan állapotban.', '87000.00', NULL, 2, 'active', 10, '2025-04-14 11:34:03', NULL, '', NULL),
(151, 'Yashica Mat-124 G', 'yashica-mat-124-g', 'Vásárolható termék: Yashica Mat-124 G', 'Kétobjektíves tükörreflexes középformátumú fényképezőgép.', '96000.00', NULL, 2, 'active', 10, '2025-04-14 11:34:03', NULL, '', NULL),
(152, 'Fujica ST605N', 'fujica-st605n', 'Vásárolható termék: Fujica ST605N', 'SLR filmes gép, fix objektívvel.', '43000.00', NULL, 4, 'active', 10, '2025-04-14 11:34:03', NULL, '', NULL),
(153, 'Nintendo Entertainment System (NES)', 'nintendo-entertainment-system-nes', 'Vásárolható termék: Nintendo Entertainment System (NES)', 'Az 1980-as évek ikonikus konzolja, eredeti kontrollerrel.', '55000.00', NULL, 4, 'active', 11, '2025-04-14 11:34:03', NULL, '', NULL),
(154, 'Super Nintendo (SNES)', 'super-nintendo-snes', 'Vásárolható termék: Super Nintendo (SNES)', 'A 16 bites korszak klasszikusa, számos legendás játékkal.', '60000.00', NULL, 5, 'active', 11, '2025-04-14 11:34:03', NULL, '', NULL),
(155, 'Sega Mega Drive II', 'sega-mega-drive-ii', 'Vásárolható termék: Sega Mega Drive II', 'A SEGA 16 bites konzolja, Sonic játékokkal ismert.', '48000.00', NULL, 6, 'active', 11, '2025-04-14 11:34:03', NULL, '', NULL),
(156, 'Sony PlayStation 1 (PS1)', 'sony-playstation-1-ps1', 'Vásárolható termék: Sony PlayStation 1 (PS1)', 'Az első PlayStation, CD-s játékokkal, 1994-ből.', '45000.00', NULL, 8, 'active', 11, '2025-04-14 11:34:03', NULL, '', NULL),
(157, 'Nintendo 64', 'nintendo-64', 'Vásárolható termék: Nintendo 64', 'Első 3D-s Nintendo konzol, klasszikus kontroller dizájnnal.', '59000.00', NULL, 5, 'active', 11, '2025-04-14 11:34:03', NULL, '', NULL),
(158, 'Game Boy Classic', 'game-boy-classic', 'Vásárolható termék: Game Boy Classic', 'Hordozható, monokróm kijelzős konzol, Tetris-szel ismert.', '35000.00', NULL, 10, 'active', 11, '2025-04-14 11:34:03', NULL, '', NULL),
(159, 'Atari 2600', 'atari-2600', 'Vásárolható termék: Atari 2600', 'A videojáték-ipar egyik első otthoni konzolja.', '40000.00', NULL, 3, 'active', 11, '2025-04-14 11:34:03', NULL, '', NULL),
(160, 'Sega Dreamcast', 'sega-dreamcast', 'Vásárolható termék: Sega Dreamcast', 'SEGA utolsó konzolja, ahead of its time technológiával.', '62000.00', NULL, 4, 'active', 11, '2025-04-14 11:34:03', NULL, '', NULL),
(161, 'Neo Geo AES', 'neo-geo-aes', 'Vásárolható termék: Neo Geo AES', 'Gyűjtői ritkaság, arcade minőség otthoni konzolban.', '125000.00', NULL, 2, 'active', 11, '2025-04-14 11:34:03', NULL, '', NULL),
(162, 'PlayStation 2', 'playstation-2', 'Vásárolható termék: PlayStation 2', 'A valaha volt legnagyobb példányszámban eladott konzol.', '38000.00', NULL, 12, 'active', 11, '2025-04-14 11:34:03', NULL, '', NULL),
(163, 'LEGO Space Classic 928', 'lego-space-classic-928', 'Vásárolható termék: LEGO Space Classic 928', 'Az 1979-es klasszikus LEGO űrhajó, eredeti alkatrészekkel.', '89000.00', NULL, 2, 'active', 12, '2025-04-14 11:34:03', NULL, '', NULL),
(164, 'Rubik Kocka - eredeti 1980-as kiadás', 'rubik-kocka-eredeti-1980', 'Vásárolható termék: Rubik Kocka - eredeti 1980-as kiadás', 'Magyar találmány, a világ egyik legismertebb logikai játéka.', '12000.00', NULL, 15, 'active', 12, '2025-04-14 11:34:03', NULL, '', NULL),
(165, 'Matchbox kisautó - 1975', 'matchbox-kisauto-1975', 'Vásárolható termék: Matchbox kisautó - 1975', 'Fém kisautó gyűjtőknek, eredeti csomagolás nélkül.', '8500.00', NULL, 20, 'active', 12, '2025-04-14 11:34:03', NULL, '', NULL),
(166, 'Playmobil Lovagvár (1984)', 'playmobil-lovagvar-1984', 'Vásárolható termék: Playmobil Lovagvár (1984)', 'Teljes készlet retro lovag figurákkal és kiegészítőkkel.', '47000.00', NULL, 4, 'active', 12, '2025-04-14 11:34:03', NULL, '', NULL),
(167, 'Monchhichi plüssfigura', 'monchhichi-plussfigura', 'Vásárolható termék: Monchhichi plüssfigura', '1980-as évek japán eredetű gyűjtői játék.', '15000.00', NULL, 7, 'active', 12, '2025-04-14 11:34:03', NULL, '', NULL),
(168, 'G.I. Joe figura - Cobra Commander', 'gi-joe-figura-cobra-commander', 'Vásárolható termék: G.I. Joe figura - Cobra Commander', 'Eredeti 1986-os G.I. Joe figura mozgatható végtagokkal.', '22000.00', NULL, 5, 'active', 12, '2025-04-14 11:34:03', NULL, '', NULL),
(169, 'Transformers G1 Megatron', 'transformers-g1-megatron', 'Vásárolható termék: Transformers G1 Megatron', '1984-es eredeti Hasbro Transformers figura.', '38000.00', NULL, 3, 'active', 12, '2025-04-14 11:34:03', NULL, '', NULL),
(170, 'Nintendo Game & Watch – Donkey Kong', 'nintendo-game-watch-donkey-kong', 'Vásárolható termék: Nintendo Game & Watch – Donkey Kong', 'Kézi LCD játék a korai 1980-as évekből.', '42000.00', NULL, 2, 'active', 12, '2025-04-14 11:34:03', NULL, '', NULL),
(171, 'Barbie - 1960-as Vintage kiadás', 'barbie-1960-vintage-kiadas', 'Vásárolható termék: Barbie - 1960-as Vintage kiadás', 'Klasszikus Barbie baba korhű ruhával, gyűjtői állapotban.', '65000.00', NULL, 2, 'active', 12, '2025-04-14 11:34:03', NULL, '', NULL),
(172, 'Tamagotchi - eredeti Bandai', 'tamagotchi-eredeti-bandai', 'Vásárolható termék: Tamagotchi - eredeti Bandai', 'Digitális kisállat 1997-ből, működőképes állapotban.', '18000.00', NULL, 6, 'active', 12, '2025-04-14 11:34:03', NULL, '', NULL),
(173, 'Kínai Porcelán Váza', 'kinai-porcelan-vaza', 'Vásárolható termék: Kínai Porcelán Váza', 'Kézzel festett, tradicionális kék-fehér kínai porcelán váza.', '120000.00', NULL, 3, 'active', 13, '2025-04-14 11:34:03', NULL, '', NULL),
(174, 'Herendi Teás Készlet', 'herendi-teas-keszlet', 'Vásárolható termék: Herendi Teás Készlet', 'Exkluzív magyar porcelán szett virágmintával, 6 személyes.', '250000.00', NULL, 2, 'active', 13, '2025-04-14 11:34:03', NULL, '', NULL),
(175, 'Japán Porcelán Tányér', 'japan-porcelan-tanyer', 'Vásárolható termék: Japán Porcelán Tányér', 'Finoman kidolgozott tányér, kék-fehér hullámmintával.', '85000.00', NULL, 5, 'active', 13, '2025-04-14 11:34:03', NULL, '', NULL),
(176, 'Vintage Porcelán Csésze', 'vintage-porcelan-cesze', 'Vásárolható termék: Vintage Porcelán Csésze', 'Antik kézzel festett csésze aranyszegéllyel.', '35000.00', NULL, 10, 'active', 13, '2025-04-14 11:34:03', NULL, '', NULL),
(177, 'Porcelán Ékszerdoboz', 'porcelan-ekszerdoboz', 'Vásárolható termék: Porcelán Ékszerdoboz', 'Dekoratív, kisméretű dobozka virágmintás porcelánból.', '65000.00', NULL, 8, 'active', 13, '2025-04-14 11:34:03', NULL, '', NULL),
(178, 'Barokk Stílusú Porcelán Gyertyatartó', 'barokk-stilusú-porcelan-gyertyatarto', 'Vásárolható termék: Barokk Stílusú Porcelán Gyertyatartó', 'Díszes, aranyozott részletekkel gazdagított gyertyatartó.', '95000.00', NULL, 4, 'active', 13, '2025-04-14 11:34:03', NULL, '', NULL),
(179, 'Antik Francia Porcelán Tál', 'antik-francia-porcelan-tal', 'Vásárolható termék: Antik Francia Porcelán Tál', 'Kézi festés és aranyozott perem jellemzi ezt a klasszikus tálat.', '130000.00', NULL, 3, 'active', 13, '2025-04-14 11:34:03', NULL, '', NULL),
(180, 'Porcelán Fali Dekoráció', 'porcelan-fali-dekoracio', 'Vásárolható termék: Porcelán Fali Dekoráció', 'Faliképként is használható porcelán dísztárgy.', '78000.00', NULL, 6, 'active', 13, '2025-04-14 11:34:03', NULL, '', NULL),
(181, 'Kézzel Festett Porcelán Tálca', 'kezellel-festett-porcelan-talca', 'Vásárolható termék: Kézzel Festett Porcelán Tálca', 'Egyedi festésű tálca, klasszikus európai motívumokkal.', '112000.00', NULL, 5, 'active', 13, '2025-04-14 11:34:03', NULL, '', NULL),
(182, 'Modern Minimalista Porcelán Edénykészlet', 'modern-minimalista-porcelan-edenykeszlet', 'Vásárolható termék: Modern Minimalista Porcelán Edénykészlet', 'Letisztult dizájn, matt fehér bevonattal, 4 részes.', '145000.00', NULL, 7, 'active', 13, '2025-04-14 11:34:03', NULL, '', NULL),
(183, 'Vintage Bőrönd - Barna Bőr', 'vintage-borond-barna-bor', 'Vásárolható termék: Vintage Bőrönd - Barna Bőr', 'Kézzel varrott vintage bőrönd réz csatokkal, 1950-es évek.', '78000.00', NULL, 2, 'active', 14, '2025-04-14 11:34:03', NULL, '', NULL),
(184, 'Retro Textil Bőrönd', 'retro-textil-borond', 'Vásárolható termék: Retro Textil Bőrönd', 'Klasszikus kockás mintázatú utazótáska merevítéssel.', '42000.00', NULL, 4, 'active', 14, '2025-04-14 11:34:03', NULL, '', NULL),
(185, 'Régi Fém Koffer', 'regi-fem-koffer', 'Vásárolható termék: Régi Fém Koffer', 'Ipari stílusú, strapabíró fém koffer fogantyúval.', '58000.00', NULL, 3, 'active', 14, '2025-04-14 11:34:03', NULL, '', NULL),
(186, 'Vintage Utazóláda', 'vintage-utazolada', 'Vásárolható termék: Vintage Utazóláda', 'Fából készült nagyméretű láda utazásokhoz, rézveretekkel.', '96000.00', NULL, 1, 'active', 14, '2025-04-14 11:34:03', NULL, '', NULL),
(187, 'Kézipoggyász - 1960-as évek', 'kezipoggyasz-1960-as-evek', 'Vásárolható termék: Kézipoggyász - 1960-as évek', 'Retro mintás kézitáska műbőrből, korhű állapotban.', '29000.00', NULL, 5, 'active', 14, '2025-04-14 11:34:03', NULL, '', NULL),
(188, 'Antik Vasúti Bőrönd', 'antik-vasuti-borond', 'Vásárolható termék: Antik Vasúti Bőrönd', 'Vasúti utazásokra használt keményfedeles táska.', '61000.00', NULL, 2, 'active', 14, '2025-04-14 11:34:03', NULL, '', NULL),
(189, 'Steamer Trunk - Régi Gőzhajós Láda', 'steamer-trunk-regi-gozhajos-lada', 'Vásárolható termék: Steamer Trunk - Régi Gőzhajós Láda', 'Gyűjtői darab klasszikus bőr utazóláda.', '120000.00', NULL, 1, 'active', 14, '2025-04-14 11:34:03', NULL, '', NULL),
(190, 'Retro Sporttáska Adidas (1970)', 'retro-sporttaska-adidas-1970', 'Vásárolható termék: Retro Sporttáska Adidas (1970)', 'Eredeti Adidas sporttáska klasszikus logóval.', '37000.00', NULL, 3, 'active', 14, '2025-04-14 11:34:03', NULL, '', NULL),
(191, 'Bőrönd Szett - háromrészes', 'borond-szett-haromreszes', 'Vásárolható termék: Bőrönd Szett - háromrészes', 'Vintage megjelenésű puha borítású bőrönd szett.', '88000.00', NULL, 2, 'active', 14, '2025-04-14 11:34:03', NULL, '', NULL),
(192, 'Hordozható Gramofon Tok', 'hordozhato-gramofon-tok', 'Vásárolható termék: Hordozható Gramofon Tok', 'Régi gramofon tárolására szolgáló hordozható tok.', '45000.00', NULL, 2, 'active', 14, '2025-04-14 11:34:03', NULL, '', NULL),
(193, 'Ezüst Evőeszköz Készlet (12 személyes)', 'ezust-evoseszkoz-keszlet-12-szemelyes', 'Vásárolható termék: Ezüst Evőeszköz Készlet (12 személyes)', '925-ös ezüstből készült klasszikus evőeszköz szett díszdobozban.', '185000.00', NULL, 1, 'active', 15, '2025-04-14 11:34:03', NULL, '', NULL),
(194, 'Antik Francia Villa és Kés Páros', 'antik-francia-villa-es-kes-paros', 'Vásárolható termék: Antik Francia Villa és Kés Páros', '1900-as évekbeli francia evőeszközök gravírozott nyéllel.', '42000.00', NULL, 3, 'active', 15, '2025-04-14 11:34:03', NULL, '', NULL),
(195, 'Angol Teáskanál Szett (6 db)', 'angol-teaskanall-szett-6-db', 'Vásárolható termék: Angol Teáskanál Szett (6 db)', 'Szecessziós stílusú teáskanalak ezüstözött kivitelben.', '29000.00', NULL, 4, 'active', 15, '2025-04-14 11:34:03', NULL, '', NULL),
(196, 'Retro Piknik Evőeszközkészlet', 'retro-piknik-evoseszkoz-keszlet', 'Vásárolható termék: Retro Piknik Evőeszközkészlet', 'Műanyag és fém evőeszközök színes tokban, 1970-es évekből.', '12000.00', NULL, 6, 'active', 15, '2025-04-14 11:34:03', NULL, '', NULL),
(197, 'Kézzel Kalapált Réz Evőeszközök', 'kezellel-kalapalt-rez-evoseszkozok', 'Vásárolható termék: Kézzel Kalapált Réz Evőeszközök', 'Különleges, kézműves réz szett étkezésekhez.', '35000.00', NULL, 2, 'active', 15, '2025-04-14 11:34:03', NULL, '', NULL),
(198, 'Art Deco Stílusú Késkészlet', 'art-deco-stilusuu-keskeszlet', 'Vásárolható termék: Art Deco Stílusú Késkészlet', 'Geometrikus mintázatú, acél pengés vintage kések.', '27000.00', NULL, 5, 'active', 15, '2025-04-14 11:34:03', NULL, '', NULL),
(199, 'Gyermek Evőeszköz Szett - 1960', 'gyermek-evoseszkoz-szett-1960', 'Vásárolható termék: Gyermek Evőeszköz Szett - 1960', 'Gravírozott állatfigurás kis méretű készlet.', '15000.00', NULL, 7, 'active', 15, '2025-04-14 11:34:03', NULL, '', NULL),
(200, 'Ezüstözött Tálalókanalak (3 db)', 'ezustozott-talalokanalak-3-db', 'Vásárolható termék: Ezüstözött Tálalókanalak (3 db)', 'Nagyméretű, díszített evőeszközök tálaláshoz.', '24000.00', NULL, 3, 'active', 15, '2025-04-14 11:34:03', NULL, '', NULL),
(201, 'Porcelán Nyelű Evőeszközök', 'porcelan-nyelu-evoseszkozok', 'Vásárolható termék: Porcelán Nyelű Evőeszközök', 'Különleges, festett nyelű evőeszköz szett.', '31000.00', NULL, 4, 'active', 15, '2025-04-14 11:34:03', NULL, '', NULL),
(202, 'Bakelit Nyelű Retro Evőeszközök', 'bakelit-nyelu-retro-evoseszkozok', 'Vásárolható termék: Bakelit Nyelű Retro Evőeszközök', '1950-es évekbeli evőeszközök színes bakelit nyéllel.', '18000.00', NULL, 6, 'active', 15, '2025-04-14 11:34:03', NULL, '', NULL),
(203, 'Antik Álló Ruhafogas', 'antik-allo-ruhafogas', 'Vásárolható termék: Antik Álló Ruhafogas', 'Tömörfa álló fogas, hajlított akasztókarokkal, 1920-as évekből.', '64000.00', NULL, 2, 'active', 16, '2025-04-14 11:34:03', NULL, '', NULL),
(204, 'Kovácsoltvas Fali Fogas', 'kovacsoltvas-fali-fogas', 'Vásárolható termék: Kovácsoltvas Fali Fogas', 'Kézzel kovácsolt vas fali akasztó.', '28000.00', NULL, 4, 'active', 16, '2025-04-14 11:34:03', NULL, '', NULL),
(205, 'Vintage Falifogas Tükörrel', 'vintage-falifogas-tukorrel', 'Vásárolható termék: Vintage Falifogas Tükörrel', 'Tükörrel kombinált, díszes fából készült fogas.', '39000.00', NULL, 3, 'active', 16, '2025-04-14 11:34:03', NULL, '', NULL),
(206, 'Retró Műanyag Fogas Szett (6 db)', 'retro-muanyag-fogas-szett-6-db', 'Vásárolható termék: Retró Műanyag Fogas Szett (6 db)', 'Színes műanyag ruhafogasok 1970-es évekből.', '9500.00', NULL, 8, 'active', 16, '2025-04-14 11:34:03', NULL, '', NULL),
(207, 'Art Deco Stílusú Ruhafogas', 'art-deco-stilusuu-ruhafogas', 'Vásárolható termék: Art Deco Stílusú Ruhafogas', 'Króm és fa kombináció, elegáns geometrikus formákkal.', '52000.00', NULL, 2, 'active', 16, '2025-04-14 11:34:03', NULL, '', NULL),
(208, 'Térelválasztós Fogas Panel', 'terelevallasztos-fogas-panel', 'Vásárolható termék: Térelválasztós Fogas Panel', 'Nagy méretű falpanel akasztókkal és kalaptartóval.', '87000.00', NULL, 1, 'active', 16, '2025-04-14 11:34:03', NULL, '', NULL),
(209, 'Fali Fogas Színes Kerámiagombbal', 'fali-fogas-sines-keramiagombbal', 'Vásárolható termék: Fali Fogas Színes Kerámiagombbal', 'Rusztikus fa alap, festett kerámia akasztófejekkel.', '33000.00', NULL, 5, 'active', 16, '2025-04-14 11:34:03', NULL, '', NULL),
(210, 'Bakelit Akasztós Fogas', 'bakelit-akasztos-fogas', 'Vásárolható termék: Bakelit Akasztós Fogas', '1950-es évekből származó fali fogas retró bakelit panelekből.', '24500.00', NULL, 3, 'active', 16, '2025-04-14 11:34:03', NULL, '', NULL),
(211, 'Kihajtható Ruhafogas', 'kihajthato-ruhafogas', 'Vásárolható termék: Kihajtható Ruhafogas', 'Falra szerelhető, fából készült, kihajtható szerkezet.', '19500.00', NULL, 6, 'active', 16, '2025-04-14 11:34:03', NULL, '', NULL),
(212, 'Gyermek Ruhafogas – Állatos Mintával', 'gyermek-ruhafogas-allatos-mintaval', 'Vásárolható termék: Gyermek Ruhafogas – Állatos Mintával', 'Kézzel festett fa fogas gyerekeknek.', '16000.00', NULL, 7, 'active', 16, '2025-04-14 11:34:03', NULL, '', NULL),
(213, 'Kovácsoltvas Keretes Fali Tükör', 'kovacsoltvas-keretes-fali-tukor', 'Vásárolható termék: Kovácsoltvas Keretes Fali Tükör', 'Kézzel kovácsolt, díszes mintázatú vas keret, rusztikus stílus.', '48000.00', NULL, 3, 'active', 17, '2025-04-14 11:34:03', NULL, '', NULL),
(214, 'Barokk Stílusú Aranyozott Tükör', 'barokk-stilusuu-aranyozott-tukor', 'Vásárolható termék: Barokk Stílusú Aranyozott Tükör', 'Részletgazdag, faragott fa keret aranyozott bevonattal.', '88000.00', NULL, 2, 'active', 17, '2025-04-14 11:34:03', NULL, '', NULL),
(215, 'Ovális Asztali Sminktükör', 'ovalis-asztali-sminktukor', 'Vásárolható termék: Ovális Asztali Sminktükör', 'Rézállványos, dönthető ovális tükör, 1920-as évekből.', '32000.00', NULL, 4, 'active', 17, '2025-04-14 11:34:03', NULL, '', NULL),
(216, 'Vintage Fali Tükör Faragott Keretben', 'vintage-fali-tukor-faragott-keretben', 'Vásárolható termék: Vintage Fali Tükör Faragott Keretben', 'Díszes, kézzel faragott keret, patinás felület.', '55000.00', NULL, 3, 'active', 17, '2025-04-14 11:34:03', NULL, '', NULL),
(217, 'Retró Kerek Tükör Fa Kerettel', 'retro-kerek-tukor-fa-kerettel', 'Vásárolható termék: Retró Kerek Tükör Fa Kerettel', '1960-as évek skandináv stílusú minimalista fa keret.', '37000.00', NULL, 5, 'active', 17, '2025-04-14 11:34:03', NULL, '', NULL),
(218, 'Állótükör – Viktoriánus stílus', 'allotukor-viktorianus-stilus', 'Vásárolható termék: Állótükör – Viktoriánus stílus', 'Nagy méretű, díszített lábas tükör, klasszikus hálószobába.', '94000.00', NULL, 1, 'active', 17, '2025-04-14 11:34:03', NULL, '', NULL);
INSERT INTO `products` (`product_id`, `name`, `slug`, `meta_title`, `description`, `price`, `discount_price`, `stock_quanty`, `status`, `category_id`, `created_at`, `updatedAt`, `image_url`, `deleted_at`) VALUES
(219, 'Kézitükör – Ezüst Nyéllel', 'kezitukor-ezust-nyellel', 'Vásárolható termék: Kézitükör – Ezüst Nyéllel', 'Antik kézitükör vésett ezüst nyéllel és elegáns kialakítással.', '29000.00', NULL, 6, 'active', 17, '2025-04-14 11:34:03', NULL, '', NULL),
(220, 'Keret nélküli Fali Tükör (1950)', 'keret-nelkuli-fali-tukor-1950', 'Vásárolható termék: Keret nélküli Fali Tükör (1950)', 'Egyszerű, fazettás üvegperemmel ellátott vintage darab.', '22000.00', NULL, 4, 'active', 17, '2025-04-14 11:34:03', NULL, '', NULL),
(221, 'Napmotívumos Dekortükör', 'napmotivumos-dekortukor', 'Vásárolható termék: Napmotívumos Dekortükör', 'Dekoratív tükör fém sugármintás kerettel.', '41000.00', NULL, 3, 'active', 17, '2025-04-14 11:34:03', NULL, '', NULL),
(222, 'Antik Szalon Tükör', 'antik-szalon-tukor', 'Vásárolható termék: Antik Szalon Tükör', 'Nagy, ovális álló tükör hajlított fa lábazattal.', '79000.00', NULL, 2, 'active', 17, '2025-04-14 11:34:03', NULL, '', NULL),
(223, 'Antik Fa Könyvtámasz Pár', 'antik-fa-konyvtamash-par', 'Vásárolható termék: Antik Fa Könyvtámasz Pár', 'Kézzel faragott sötét diófából készült könyvtámaszok.', '36000.00', NULL, 3, 'active', 18, '2025-04-14 11:34:03', NULL, '', NULL),
(224, 'Vintage Kovácsoltvas Könyvtartó', 'vintage-kovacsoltvas-konyvtarto', 'Vásárolható termék: Vintage Kovácsoltvas Könyvtartó', 'Kézzel kovácsolt fém állvány, díszített mintával.', '32000.00', NULL, 4, 'active', 18, '2025-04-14 11:34:03', NULL, '', NULL),
(225, 'Art Deco Könyvállvány', 'art-deco-konyvallvany', 'Vásárolható termék: Art Deco Könyvállvány', 'Geometrikus formák, fa és fém kombináció, 1930-as évekből.', '47000.00', NULL, 2, 'active', 18, '2025-04-14 11:34:03', NULL, '', NULL),
(226, 'Fa Asztali Könyvolvasó Állvány', 'fa-asztali-konyvolvaso-allvany', 'Vásárolható termék: Fa Asztali Könyvolvasó Állvány', 'Dönthető asztali állvány könyvek vagy receptek tartásához.', '22000.00', NULL, 6, 'active', 18, '2025-04-14 11:34:03', NULL, '', NULL),
(227, 'Retro Plexi Könyvtartó', 'retro-plexi-konyvtarto', 'Vásárolható termék: Retro Plexi Könyvtartó', '1970-es évekbeli átlátszó plexiből készült darab.', '18000.00', NULL, 5, 'active', 18, '2025-04-14 11:34:03', NULL, '', NULL),
(228, 'Kézzel Festett Könyvtámasz', 'kezellel-festett-konyvtamasz', 'Vásárolható termék: Kézzel Festett Könyvtámasz', 'Színes virágmintával díszített kézműves fa könyvtámasz pár.', '26000.00', NULL, 4, 'active', 18, '2025-04-14 11:34:03', NULL, '', NULL),
(229, 'Bakelit Lemezformájú Könyvtartó', 'bakelit-lemezoformaju-konyvtarto', 'Vásárolható termék: Bakelit Lemezformájú Könyvtartó', 'Kreatív könyvtámasz bakelit lemezek ihlette dizájnnal.', '24000.00', NULL, 3, 'active', 18, '2025-04-14 11:34:03', NULL, '', NULL),
(230, 'Minimalista Fém Könyvállvány', 'minimalista-fem-konyvallvany', 'Vásárolható termék: Minimalista Fém Könyvállvány', 'Egyszerű vonalvezetésű fekete fémállvány.', '20000.00', NULL, 7, 'active', 18, '2025-04-14 11:34:03', NULL, '', NULL),
(231, 'Vintage Üvegpolcos Könyvespolc', 'vintage-uvegpolcos-konyvespolc', 'Vásárolható termék: Vintage Üvegpolcos Könyvespolc', 'Kis méretű, üvegpolcos fali könyvtartó.', '39000.00', NULL, 2, 'active', 18, '2025-04-14 11:34:03', NULL, '', NULL),
(232, 'Vasúti Sín Darabból Készült Könyvtámasz', 'vasuti-sin-darabbol-keszult-konyvtamasz', 'Vásárolható termék: Vasúti Sín Darabból Készült Könyvtámasz', 'Egyedi ipari stílusú, nehéz fémtömb könyvtámasz.', '50000.00', NULL, 1, 'active', 18, '2025-04-14 11:34:03', NULL, '', NULL);

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `shipping_addresses`
--

CREATE TABLE `shipping_addresses` (
  `shipping_address_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `recipient_name` varchar(255) NOT NULL,
  `address_line1` varchar(255) NOT NULL,
  `address_line2` varchar(255) DEFAULT NULL,
  `city` varchar(100) NOT NULL,
  `state` varchar(100) DEFAULT NULL,
  `postal_code` varchar(20) NOT NULL,
  `country` varchar(100) NOT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `shipping_addresses`
--

INSERT INTO `shipping_addresses` (`shipping_address_id`, `user_id`, `recipient_name`, `address_line1`, `address_line2`, `city`, `state`, `postal_code`, `country`, `phone`, `created_at`, `updated_at`) VALUES
(3, 123, 'Kovács Béla Updated', 'Fő utca 1.', '3. emelet, 305', 'Budapest', 'Budapest', '1011', 'Magyarország', '06307654321', '2025-04-15 11:49:40', '2025-04-15 11:49:40'),
(4, 7, 'RetroVintage Admin', 'Kodolányi János 37', '3/ 7', 'Pécs', 'Baranya', '7632', 'Magyarország', '06501246417', '2025-04-15 12:35:45', '2025-04-15 13:22:22'),
(5, 12, 'Kovács Béla Updated', 'Fő utca 1.', '3. emelet, 305', 'Budapest', 'Budapest', '1011', 'Magyarország', '06307654321', '2025-04-15 13:00:06', '2025-04-15 13:20:04'),
(9, 23, 'Gál Martin Ferenc', 'Kodolányi János 37', '3/ 7', 'Pécs', 'Baranya', '7632', 'Magyarország', '06501246417', '2025-04-15 13:23:14', '2025-04-15 13:37:01');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `test_feedback`
--

CREATE TABLE `test_feedback` (
  `feedback_id` int(8) NOT NULL,
  `order_id` int(8) NOT NULL,
  `product_id` int(8) NOT NULL,
  `is_admin` tinyint(1) NOT NULL,
  `feedback_text` text NOT NULL,
  `rating` int(11) NOT NULL,
  `created_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `tickets`
--

CREATE TABLE `tickets` (
  `ticket_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `ticket_body` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `tickets`
--

INSERT INTO `tickets` (`ticket_id`, `user_id`, `ticket_body`, `status`, `created_at`) VALUES
(2, 23, 'Jól működik a ticket!', 'Pending', '2025-04-07 19:51:33'),
(14, 7, 'Ha az adminnak van valami ötlete/ gondja az oldallal kapcsolatban akkor itt írja le!', 'Open', '2025-04-28 20:10:03'),
(15, 7, 'csak az Adminok látják és tudnak csinálni ticketet!', 'Open', '2025-04-28 20:10:38');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `username` varchar(100) NOT NULL,
  `firstname` varchar(100) NOT NULL,
  `lastname` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone_number` varchar(30) NOT NULL,
  `password` text NOT NULL,
  `is_admin` tinyint(1) NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deleted_at` timestamp NULL DEFAULT NULL,
  `profile_picture` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `user`
--

INSERT INTO `user` (`id`, `username`, `firstname`, `lastname`, `email`, `phone_number`, `password`, `is_admin`, `is_deleted`, `created_at`, `deleted_at`, `profile_picture`) VALUES
(1, 'hermannmate420', 'Mate', 'Hermann', 'hermate67@gmail.com', '+36074088704', '58b080f4d0240741b9ef583fc1106e9bcabf2042', 1, 1, '2025-02-12 01:01:40', '2025-04-05 11:21:20', NULL),
(2, 'NemvagyokMate', 'Énvagyok', 'nemMate', 'hddgamer88@gmail.com', '+3674646464', '664819d8c5343676c9225b5ed00a5cdc6f3a1ff3', 0, 0, '2025-02-25 12:00:00', NULL, NULL),
(3, 'NotAdmin', 'Gábor', 'Attila', 'attila.gabor@gmail.com', '+3456642257', '510bf3284d7f0f3cb996c2e0163d7e86aee40a69', 0, 0, '2025-03-04 18:01:59', NULL, NULL),
(4, 'Tesztelek54', 'Teszt', 'Elek', 'tesztelek@gmail.com', '+3670585895', '803de60414f888d695aadd09de69a78f8b470547', 1, 0, '2025-03-07 11:14:02', NULL, NULL),
(5, 'TesztAdmin', 'Admin', 'Teszt', 'AdminTeszt@gmail.com', '+3673777373', '5c8956161238b0a10da03c7bfe4f152e15bb1d70', 1, 0, '2025-03-11 10:52:56', NULL, NULL),
(6, 'AdminTeszt', 'Teszt', 'Admin', 'TesztAdmin@gmail.com', '+3673754543', 'd5198e4889a9365dc467262fdef7643158b05a9a', 1, 0, '2025-03-11 11:50:54', NULL, NULL),
(7, 'Admin', 'Admin', 'Admin', 'aretrovintage@gmail.com', '06501246417', '664819d8c5343676c9225b5ed00a5cdc6f3a1ff3', 1, 0, '2025-03-25 21:02:03', NULL, 'C:\\wildfly\\standalone\\deployments\\uploads\\\\profile_7_1743021774337.png'),
(23, 'MartinGal2003', 'Gál', 'Martin Ferenc', 'martingal2003@gmail.com', '06501246417', '664819d8c5343676c9225b5ed00a5cdc6f3a1ff3', 0, 0, '2025-03-26 12:28:11', NULL, 'C:\\wildfly\\standalone\\deployments\\uploads\\\\profile_23_1743187040622.jpg'),
(24, 'ehsan7813', 'sakhtemani ', 'Ehsan', 'ehsan1811378@gmail.com', '9876543210', '664819d8c5343676c9225b5ed00a5cdc6f3a1ff3', 0, 0, '2025-03-26 12:34:16', NULL, NULL),
(25, 'Sztella', 'Kotlácsik-Szincsák', 'Eszter', 'galneeszter@gmail.com', '06303074386', '34d062a81881c838f5447559cbb93d2d333226a6', 0, 0, '2025-03-27 18:41:14', NULL, NULL),
(26, 'asdasd', 'asd', 'asd', 'asdasd@asd.asd', '9876543210', '664819d8c5343676c9225b5ed00a5cdc6f3a1ff3', 0, 0, '2025-03-27 19:49:51', NULL, NULL),
(27, 'aaaaa', 'aaaaa', 'aaaaaaaaaaa', 'aaaa@aaaa.com', '9876543210', '664819d8c5343676c9225b5ed00a5cdc6f3a1ff3', 0, 1, '2025-03-27 19:50:09', '2025-04-05 11:22:18', NULL),
(28, 'bbb', 'bbb', 'bbb', 'bbb@bbb.bbb', '9876543210', '664819d8c5343676c9225b5ed00a5cdc6f3a1ff3', 0, 1, '2025-03-27 19:50:27', '2025-04-02 08:38:10', NULL),
(29, 'RabdomAdmin', 'Random', 'Admin', 'random@random.com', '9876543210', '664819d8c5343676c9225b5ed00a5cdc6f3a1ff3', 1, 0, '2025-03-28 19:43:52', NULL, NULL),
(33, 'MrTeacherMan', 'asd', 'Steve', 'boderop931@flektel.com', '9876543210', '664819d8c5343676c9225b5ed00a5cdc6f3a1ff3', 0, 0, '2025-03-31 13:17:44', NULL, 'C:\\wildfly\\standalone\\deployments\\uploads\\\\profile_33_1743427129599.jpg'),
(34, 'zalank22', 'Zalán', 'Káplár', 'kaplarzalan@gmail.com', '06303124092', 'd61a9853175a46855ecab57a36ab686b1e18bb85', 0, 0, '2025-04-03 15:25:39', NULL, 'C:\\wildfly\\standalone\\deployments\\uploads\\\\profile_34_1743694115562.jpeg'),
(35, 'testBela007', 'Bela', 'Test', 'noheso4029@dpcos.com', '9876543210', '664819d8c5343676c9225b5ed00a5cdc6f3a1ff3', 0, 0, '2025-04-13 15:53:35', NULL, NULL),
(36, 'MartinVagyokTeszt', 'Martin', 'Vagyok', 'gal.martin.ferenc@simonyiszki.org', '9876543210', '664819d8c5343676c9225b5ed00a5cdc6f3a1ff3', 0, 0, '2025-04-13 15:56:24', NULL, NULL);

--
-- Indexek a kiírt táblákhoz
--

--
-- A tábla indexei `blogs`
--
ALTER TABLE `blogs`
  ADD PRIMARY KEY (`blog_id`);

--
-- A tábla indexei `cart`
--
ALTER TABLE `cart`
  ADD PRIMARY KEY (`cartId`),
  ADD KEY `carts_user` (`userId`);

--
-- A tábla indexei `cart_items`
--
ALTER TABLE `cart_items`
  ADD PRIMARY KEY (`cartItem_id`),
  ADD KEY `cart_items_cart` (`cartId`),
  ADD KEY `cart_items_product` (`productId`);

--
-- A tábla indexei `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`category_id`);

--
-- A tábla indexei `comments`
--
ALTER TABLE `comments`
  ADD PRIMARY KEY (`comment_id`);

--
-- A tábla indexei `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`message_id`);

--
-- A tábla indexei `newsletter_subscribers`
--
ALTER TABLE `newsletter_subscribers`
  ADD PRIMARY KEY (`subscriber_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- A tábla indexei `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_orders_user` (`user_id`);

--
-- A tábla indexei `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`order_item_id`);

--
-- A tábla indexei `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`payment_id`);

--
-- A tábla indexei `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`product_id`);

--
-- A tábla indexei `shipping_addresses`
--
ALTER TABLE `shipping_addresses`
  ADD PRIMARY KEY (`shipping_address_id`);

--
-- A tábla indexei `test_feedback`
--
ALTER TABLE `test_feedback`
  ADD PRIMARY KEY (`feedback_id`);

--
-- A tábla indexei `tickets`
--
ALTER TABLE `tickets`
  ADD PRIMARY KEY (`ticket_id`);

--
-- A tábla indexei `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- A kiírt táblák AUTO_INCREMENT értéke
--

--
-- AUTO_INCREMENT a táblához `blogs`
--
ALTER TABLE `blogs`
  MODIFY `blog_id` int(8) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=53;

--
-- AUTO_INCREMENT a táblához `cart`
--
ALTER TABLE `cart`
  MODIFY `cartId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT a táblához `cart_items`
--
ALTER TABLE `cart_items`
  MODIFY `cartItem_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT a táblához `categories`
--
ALTER TABLE `categories`
  MODIFY `category_id` int(8) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT a táblához `comments`
--
ALTER TABLE `comments`
  MODIFY `comment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT a táblához `messages`
--
ALTER TABLE `messages`
  MODIFY `message_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT a táblához `newsletter_subscribers`
--
ALTER TABLE `newsletter_subscribers`
  MODIFY `subscriber_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT a táblához `orders`
--
ALTER TABLE `orders`
  MODIFY `id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT a táblához `order_items`
--
ALTER TABLE `order_items`
  MODIFY `order_item_id` int(8) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT a táblához `payments`
--
ALTER TABLE `payments`
  MODIFY `payment_id` int(8) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `products`
--
ALTER TABLE `products`
  MODIFY `product_id` int(8) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=234;

--
-- AUTO_INCREMENT a táblához `shipping_addresses`
--
ALTER TABLE `shipping_addresses`
  MODIFY `shipping_address_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT a táblához `test_feedback`
--
ALTER TABLE `test_feedback`
  MODIFY `feedback_id` int(8) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `tickets`
--
ALTER TABLE `tickets`
  MODIFY `ticket_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT a táblához `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- Megkötések a kiírt táblákhoz
--

--
-- Megkötések a táblához `cart`
--
ALTER TABLE `cart`
  ADD CONSTRAINT `carts_user` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Megkötések a táblához `cart_items`
--
ALTER TABLE `cart_items`
  ADD CONSTRAINT `cart_items_cart` FOREIGN KEY (`cartId`) REFERENCES `cart` (`cartId`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `cart_items_product` FOREIGN KEY (`productId`) REFERENCES `products` (`product_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Megkötések a táblához `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
