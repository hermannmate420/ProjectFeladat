-- phpMyAdmin SQL Dump
-- version 5.1.2
-- https://www.phpmyadmin.net/
--
-- Gép: localhost:3306
-- Létrehozás ideje: 2025. Már 10. 12:47
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

CREATE DEFINER=`root`@`localhost` PROCEDURE `addProduct` (IN `nameIN` VARCHAR(100), IN `descIN` TEXT, IN `priceIN` DOUBLE, IN `quantityIN` INT, IN `categoryNameIN` VARCHAR(100))   BEGIN
    DECLARE categoryId INT;

    -- Kategória ID keresése
    SELECT `categories`.`category_id` INTO categoryId
    FROM categories
    WHERE name = categoryNameIN;

    -- Ha a kategória nem létezik, hibaüzenet
    IF categoryId IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Kategória nem található';
    END IF;

    -- Termék beszúrása a products táblába
    INSERT INTO products (name, description, price, stock_quanty, category_id)
    VALUES (nameIN, descIN, priceIN, quantityIN, categoryId);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllUser` ()   SELECT * FROM `user`$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getProductByID` (IN `idIN` INT(8))   SELECT *
FROM `products` 
WHERE idIN = `products`.`product_id`$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserById` (IN `idIN` INT(8))   SELECT * FROM `user`
WHERE `user`.`id` = idIN$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `isUserExists` (IN `emailIN` VARCHAR(100), OUT `resultOUT` BOOLEAN)   BEGIN
DECLARE user_count INT;

    SELECT COUNT(*) INTO user_count 
    FROM user 
    WHERE email = emailIN;

    IF user_count > 0 THEN
        SET resultOUT = TRUE;
    ELSE
        SET resultOUT = FALSE;
    END IF;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `login` (IN `email` VARCHAR(100), IN `password` VARCHAR(100))   SELECT * FROM `user`
WHERE `user`.`email` = email AND `user`.`password` = sha1(password)$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `registration` (IN `userIN` VARCHAR(100), IN `firstnameIN` VARCHAR(100), IN `lastnameIN` VARCHAR(100), IN `emailIN` VARCHAR(100), IN `phoneIN` VARCHAR(100), IN `passwordIN` VARCHAR(200))   INSERT INTO `user`(`User`.`username`, `User`.`firstname`, `User`.`lastname`,`user`.`email`, `user`.`phone_number`, `user`.`password`, `user`.`is_admin`, `user`.`is_deleted`) VALUES (userIN, firstnameIN, lastnameIN,emailIN, phoneIN, SHA1(passwordIN),0,0)$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `users_uppdate` (IN `userIdIN` INT(11), IN `userIN` VARCHAR(100), IN `passwordIN` VARCHAR(255), IN `emailIN` VARCHAR(255), IN `isadminIN` BOOLEAN, IN `isdeletedIN` BOOLEAN)   UPDATE `user`
SET  `user`.`username`= userIN,
	`user`.`password` = passwordIN,
    `user`.`email` = emailIN,
    `user`.`is_admin` = isadminIN,
    `user`.`is_deleted` = isdeletedIN
WHERE `user`.`user_id` = userIdIN$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `user_delete` (IN `userIN` INT(11))   UPDATE `users` 
SET `users`.`is_deleted` = 1,
	`users`.`deleted_at` = CURRENT_TIMESTAMP
WHERE `users`.`user_id` = userIN$$

DELIMITER ;

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
-- Tábla szerkezet ehhez a táblához `orders`
--

CREATE TABLE `orders` (
  `order_id` int(8) NOT NULL,
  `user_id` int(8) NOT NULL,
  `order_date` datetime DEFAULT NULL,
  `total_amount` decimal(10,0) NOT NULL,
  `status` varchar(50) NOT NULL,
  `is_test_order` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `order_items`
--

CREATE TABLE `order_items` (
  `order_item_id` int(8) NOT NULL,
  `order_id` int(8) NOT NULL,
  `product_id` int(8) NOT NULL,
  `quantity` int(11) NOT NULL,
  `unit_price` decimal(10,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
  `description` text NOT NULL,
  `price` decimal(10,0) NOT NULL,
  `stock_quanty` int(11) NOT NULL,
  `category_id` int(8) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `products`
--

INSERT INTO `products` (`product_id`, `name`, `description`, `price`, `stock_quanty`, `category_id`, `created_at`) VALUES
(1, 'Cartier óra', 'A világ első Cartier órája', '60', 2, 1, '2024-12-05 13:54:51'),
(2, 'Antik Fa Fali Óra', 'Klasszikus antik falióra kézi faragással.', '46', 10, 1, '2024-12-05 14:00:42'),
(3, 'Retro Zsebóra', 'Ezüst zsebóra lánccal és gravírozott mintával.', '30', 15, 1, '2024-12-05 14:00:42'),
(4, 'Aranyozott Asztali Óra', 'Elegáns aranyozott óra, viktoriánus stílusban.', '113', 2, 1, '2024-12-05 14:00:42'),
(5, 'Vintage Gyöngysor', 'Klasszikus gyöngysor kézzel készült díszítéssel.', '20', 20, 2, '2024-12-05 14:00:42'),
(6, 'Ezüst Karkötő', 'Kézzel készített ezüst karkötő egyedi mintával.', '25', 12, 2, '2024-12-05 14:00:42'),
(7, 'Antik Gyűrű', 'Aranyozott gyűrű apró kövekkel díszítve.', '50', 1, 2, '2024-12-05 14:00:42'),
(8, 'Kovácsoltvas Gyertyatartó', 'Egyedi, kézzel készült kovácsoltvas gyertyatartó.', '35', 10, 3, '2024-12-05 14:00:42'),
(9, 'Bronz Asztali Gyertyatartó', 'Antik bronz gyertyatartó, 2 szál gyertyához.', '45', 7, 3, '2024-12-05 14:00:42'),
(10, 'Kristály Gyertyatartó', 'Elegáns kristály gyertyatartó díszdobozban.', '60', 5, 3, '2024-12-05 14:00:42'),
(11, 'Bőr Oldaltáska', 'Kézzel készült barna bőrtáska, vintage dizájnnal.', '80', 10, 4, '2024-12-05 14:00:42'),
(12, 'Retro Vászontáska', 'Nosztalgikus mintázatú vászontáska.', '20', 25, 4, '2024-12-05 14:00:42'),
(13, 'Antik Kézitáska', 'Különleges, kézzel varrott kézitáska réz csatokkal.', '50', 7, 4, '2024-12-05 14:00:42'),
(14, 'Retro Lemezjátszó', 'Klasszikus fa burkolatú lemezjátszó.', '160', 5, 5, '2024-12-05 14:00:42'),
(15, 'Hordozható Lemezjátszó', 'Vintage stílusú kompakt lemezjátszó.', '90', 8, 5, '2024-12-05 14:00:42'),
(16, 'Hi-Fi Lemezjátszó', 'Prémium minőségű retro lemezjátszó.', '200', 3, 5, '2024-12-05 14:00:42'),
(17, 'Beatles Bakelit', 'Eredeti Beatles album bakeliten.', '40', 15, 6, '2024-12-05 14:00:42'),
(18, 'Elvis Presley Bakelit', 'Elvis klasszikus dalai bakeliten.', '35', 12, 6, '2024-12-05 14:00:42'),
(19, 'Queen Gyűjtemény', 'Queen legjobb dalai exkluzív bakeliten.', '50', 8, 6, '2024-12-05 14:00:42'),
(20, 'Antik Asztali Lámpa', 'Kézzel készített bronz asztali lámpa.', '80', 10, 7, '2024-12-05 14:00:42'),
(21, 'Retro Állólámpa', 'Klasszikus állólámpa vintage stílusban.', '130', 5, 7, '2024-12-05 14:00:42'),
(22, 'Színes Üveglámpa', 'Tiffany stílusú színes üveglámpa.', '90', 7, 7, '2024-12-05 14:00:42'),
(23, 'Fém Fali Dekoráció', 'Kovácsoltvas fali dekoráció virágmintával.', '30', 12, 8, '2024-12-05 14:00:42'),
(24, 'Retro Plakát', 'Vintage filmplakát fakeretben.', '20', 20, 8, '2024-12-05 14:00:42'),
(25, 'Antik Tükör', 'Különleges kovácsoltvas keretű falitükör.', '80', 6, 8, '2024-12-05 14:00:42'),
(26, 'Retro Bögre Szett', 'Klasszikus mintázatú kerámiabögrék szettje.', '25', 15, 9, '2024-12-05 14:00:42'),
(27, 'Egyedi Nyomtatott Bögre', 'Kézzel festett retro bögre.', '15', 25, 9, '2024-12-05 14:00:42'),
(28, 'Antik Porcelán Bögre', 'Elegáns, arany szélű porcelán bögre.', '20', 10, 9, '2024-12-05 14:00:42'),
(29, 'Polaroid Kamera', 'Vintage Polaroid instant fényképezőgép.', '90', 10, 10, '2024-12-05 14:00:42'),
(30, 'Canon Filmkamera', 'Klasszikus Canon 35mm-es filmkamera.', '130', 5, 10, '2024-12-05 14:00:42'),
(31, 'Retro Videokamera', 'Antik videokamera gyűjtőknek.', '150', 3, 10, '2024-12-05 14:00:42'),
(32, 'Atari 2600', 'Klasszikus Atari 2600 játékkonzol.', '100', 8, 11, '2024-12-05 14:00:42'),
(33, 'Nintendo NES', 'Eredeti Nintendo NES konzol.', '130', 6, 11, '2024-12-05 14:00:42'),
(34, 'Sega Genesis', 'Retro Sega konzol gyűjtők számára.', '90', 10, 11, '2024-12-05 14:00:42'),
(35, 'Monopoly Retro Kiadás', 'Klasszikus Monopoly társasjáték.', '50', 15, 12, '2024-12-05 14:00:42'),
(36, 'Vintage Puzzle', '1000 darabos antik stílusú puzzle.', '20', 20, 12, '2024-12-05 14:00:42'),
(37, 'Gyűjtői Akciófigura', 'Limitált kiadású vintage akciófigura.', '60', 5, 12, '2024-12-05 14:00:42'),
(38, 'Antik Porcelán Váza', 'Részletesen díszített antik váza.', '80', 7, 13, '2024-12-05 14:00:42'),
(39, 'Porcelán Teás Készlet', 'Klasszikus porcelán teáskészlet.', '100', 5, 13, '2024-12-05 14:00:42'),
(40, 'Dekoratív Porcelán Tányér', 'Elegáns, mintás porcelántányér.', '25', 12, 13, '2024-12-05 14:00:42'),
(41, 'Antik Bőrönd', 'Kézzel készített vintage bőrönd.', '130', 5, 14, '2024-12-05 14:00:42'),
(42, 'Retro Fa Utazóláda', 'Egyedi, kézzel készített fa utazóláda.', '150', 3, 14, '2024-12-05 14:00:42'),
(43, 'Vintage Kézipoggyász', 'Klasszikus stílusú kézipoggyász.', '90', 8, 14, '2024-12-05 14:00:42'),
(44, 'Ezüst Evőeszköz Készlet', 'Klasszikus ezüst evőeszközök.', '60', 10, 15, '2024-12-05 14:00:42'),
(45, 'Réz Kiskanál Szett', 'Egyedi réz kiskanalak vintage stílusban.', '20', 20, 15, '2024-12-05 14:00:42'),
(46, 'Antik Evőeszköz Szett', 'Exkluzív antik evőeszköz készlet.', '100', 5, 15, '2024-12-05 14:00:42'),
(47, 'Kovácsoltvas Fogas', 'Elegáns falra szerelhető kovácsoltvas fogas.', '35', 12, 16, '2024-12-05 14:00:42'),
(48, 'Vintage Fa Fogas', 'Kézzel készített fa ruhafogas.', '25', 15, 16, '2024-12-05 14:00:42'),
(49, 'Antik Álló Fogas', 'Klasszikus antik stílusú álló fogas.', '80', 5, 16, '2024-12-05 14:00:42'),
(50, 'Kör alakú Kovácsoltvas Tükör', 'Kézzel készített kör alakú antik tükör.', '90', 7, 17, '2024-12-05 14:00:42'),
(51, 'Díszített Falitükör', 'Vintage díszített keretű falitükör.', '100', 5, 17, '2024-12-05 14:00:42'),
(52, 'Álló Antik Tükör', 'Nagyméretű álló antik tükör.', '150', 3, 17, '2024-12-05 14:00:42');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `shipping_addresses`
--

CREATE TABLE `shipping_addresses` (
  `address_id` int(8) NOT NULL,
  `user_id` int(8) NOT NULL,
  `order_id` int(8) NOT NULL,
  `address_line1` varchar(255) NOT NULL,
  `address_line2` varchar(255) NOT NULL,
  `city` varchar(100) NOT NULL,
  `postal_code` varchar(20) NOT NULL,
  `country` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- A tábla adatainak kiíratása `user`
--

INSERT INTO `user` (`id`, `username`, `firstname`, `lastname`, `email`, `phone_number`, `password`, `is_admin`, `is_deleted`, `created_at`, `deleted_at`) VALUES
(1, 'hermannmate420', 'Mate', 'Hermann', 'hermate67@gmail.com', '+36074088704', '58b080f4d0240741b9ef583fc1106e9bcabf2042', 1, 0, '2025-02-12 01:01:40', NULL),
(2, 'Matevagyok', 'Énvagyok', 'Mate', 'hddgamer88@gmail.com', '+367848484848', '664819d8c5343676c9225b5ed00a5cdc6f3a1ff3', 0, 0, '2025-02-25 12:00:00', NULL),
(3, 'Admin', 'Admin', 'Admin', 'aretrovintage@gmail.com', '06696996', '664819d8c5343676c9225b5ed00a5cdc6f3a1ff3', 1, 0, '2025-03-10 12:03:52', NULL),
(4, 'MartinGal', 'Gál', 'Martin Ferenc', 'martingal2003@gmail.com', '+36501246417', 'bfea1585627570da7fa410b1405f96dcaff25a04', 1, 0, '2025-03-07 13:22:38', NULL),
(5, '1KZ1k', 'Káplár', 'Zalán', 'kaplarzalan@gmail.com', '06303124092', '084a025c2a5c1404577fc14105594b52dde532aa', 0, 0, '2025-03-09 18:15:48', NULL),
(7, 'panczamilan', 'Pancza', 'Milán', 'panczamilan19@gmail.com', '+36202500673', '664819d8c5343676c9225b5ed00a5cdc6f3a1ff3', 0, 0, '2025-03-10 11:36:46', NULL);

--
-- Indexek a kiírt táblákhoz
--

--
-- A tábla indexei `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`category_id`);

--
-- A tábla indexei `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_id`);

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
  ADD PRIMARY KEY (`address_id`);

--
-- A tábla indexei `test_feedback`
--
ALTER TABLE `test_feedback`
  ADD PRIMARY KEY (`feedback_id`);

--
-- A tábla indexei `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- A kiírt táblák AUTO_INCREMENT értéke
--

--
-- AUTO_INCREMENT a táblához `categories`
--
ALTER TABLE `categories`
  MODIFY `category_id` int(8) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT a táblához `orders`
--
ALTER TABLE `orders`
  MODIFY `order_id` int(8) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `order_items`
--
ALTER TABLE `order_items`
  MODIFY `order_item_id` int(8) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `payments`
--
ALTER TABLE `payments`
  MODIFY `payment_id` int(8) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `products`
--
ALTER TABLE `products`
  MODIFY `product_id` int(8) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=53;

--
-- AUTO_INCREMENT a táblához `shipping_addresses`
--
ALTER TABLE `shipping_addresses`
  MODIFY `address_id` int(8) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `test_feedback`
--
ALTER TABLE `test_feedback`
  MODIFY `feedback_id` int(8) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
