-- phpMyAdmin SQL Dump
-- version 5.1.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Dec 06, 2024 at 12:14 PM
-- Server version: 5.7.24
-- PHP Version: 8.0.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `vintage_db`
--

DELIMITER $$
--
-- Procedures
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

CREATE DEFINER=`root`@`localhost` PROCEDURE `getProductByID` (IN `idIN` INT(8))   SELECT *
FROM `products` 
WHERE idIN = `products`.`product_id`$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `users_add` (IN `userIN` VARCHAR(100), IN `passwordIN` VARCHAR(255), IN `emailIN` VARCHAR(255), IN `is_adminIN` BOOLEAN, IN `is_deletedIN` BOOLEAN)   BEGIN
INSERT INTO 
`users`(`users`.`username`, `users`.`password`, `users`.`email`, `users`.`is_admin`, `users`.`is_deleted`)
VALUES
(userIN, passwordIN, emailIN, is_adminIN, is_deletedIN);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `users_uppdate` (IN `userIdIN` INT(11), IN `userIN` VARCHAR(100), IN `passwordIN` VARCHAR(255), IN `emailIN` VARCHAR(255), IN `isadminIN` BOOLEAN, IN `isdeletedIN` BOOLEAN)   UPDATE `users`
SET `users`.`username` = userIN,
	`users`.`password` = passwordIN,
    `users`.`email` = emailIN,
    `users`.`is_admin` = isadminIN,
    `users`.`is_deleted` = isdeletedIN
WHERE `users`.`user_id` = userIdIN$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `user_delete` (IN `userIN` INT(11))   UPDATE `users` 
SET `users`.`is_deleted` = 1,
	`users`.`deleted_at` = CURRENT_TIMESTAMP
WHERE `users`.`user_id` = userIN$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `category_id` int(8) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `categories`
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
-- Table structure for table `orders`
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
-- Table structure for table `order_items`
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
-- Table structure for table `payments`
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
-- Table structure for table `products`
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
-- Dumping data for table `products`
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
-- Table structure for table `shipping_addresses`
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
-- Table structure for table `test_feedback`
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
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(8) NOT NULL,
  `username` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_admin` tinyint(1) NOT NULL,
  `is_deleted` tinyint(1) NOT NULL,
  `deleted_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password`, `email`, `created_at`, `is_admin`, `is_deleted`, `deleted_at`) VALUES
(1, 'Admin', 'Admin1234', 'admin@admin.com', '2024-10-21 15:02:20', 1, 0, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`category_id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_id`);

--
-- Indexes for table `order_items`
--
ALTER TABLE `order_items`
  ADD PRIMARY KEY (`order_item_id`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`payment_id`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`product_id`);

--
-- Indexes for table `shipping_addresses`
--
ALTER TABLE `shipping_addresses`
  ADD PRIMARY KEY (`address_id`);

--
-- Indexes for table `test_feedback`
--
ALTER TABLE `test_feedback`
  ADD PRIMARY KEY (`feedback_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `category_id` int(8) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `order_id` int(8) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `order_items`
--
ALTER TABLE `order_items`
  MODIFY `order_item_id` int(8) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `payment_id` int(8) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `product_id` int(8) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=53;

--
-- AUTO_INCREMENT for table `shipping_addresses`
--
ALTER TABLE `shipping_addresses`
  MODIFY `address_id` int(8) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `test_feedback`
--
ALTER TABLE `test_feedback`
  MODIFY `feedback_id` int(8) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(8) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
