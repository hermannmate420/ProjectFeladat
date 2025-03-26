-- phpMyAdmin SQL Dump
-- version 5.1.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Mar 26, 2025 at 12:26 PM
-- Server version: 5.7.24
-- PHP Version: 8.1.0

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
CREATE DATABASE IF NOT EXISTS `vintage_db` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `vintage_db`;

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

CREATE DEFINER=`root`@`localhost` PROCEDURE `changePassword` (IN `userIdIN` INT(8), IN `newPasswordIN` VARCHAR(255), IN `creatorIN` INT(100))   BEGIN
    UPDATE `user`
    SET `password` = SHA1(newPasswordIN)
    WHERE `id` = userIdIN;
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
    FROM `user` 
    WHERE `email` = emailIN;

    IF user_count > 0 THEN
        SET resultOUT = TRUE;
    ELSE
        SET resultOUT = FALSE;
    END IF;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `login` (IN `email` VARCHAR(100), IN `password` VARCHAR(100))   SELECT * FROM `user`
WHERE `user`.`email` = email AND `user`.`password` = sha1(password)$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `registerAdmin` (IN `userIN` VARCHAR(100), IN `firstnameIN` VARCHAR(100), IN `lastnameIN` VARCHAR(100), IN `emailIN` VARCHAR(100), IN `phoneIN` VARCHAR(100), IN `passwordIN` VARCHAR(100))   INSERT INTO `user`(`user`.`username`, `user`.`firstname`, `user`.`lastname`,`user`.`email`, `user`.`phone_number`, `user`.`password`, `user`.`is_admin`, `user`.`is_deleted`) VALUES (userIN, firstnameIN, lastnameIN,emailIN, phoneIN, SHA1(passwordIN),1,0)$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `registration` (IN `userIN` VARCHAR(100), IN `firstnameIN` VARCHAR(100), IN `lastnameIN` VARCHAR(100), IN `emailIN` VARCHAR(100), IN `phoneIN` VARCHAR(100), IN `passwordIN` VARCHAR(200))   INSERT INTO `user`(`user`.`username`, `user`.`firstname`, `user`.`lastname`,`user`.`email`, `user`.`phone_number`, `user`.`password`, `user`.`is_admin`, `user`.`is_deleted`) VALUES (userIN, firstnameIN, lastnameIN,emailIN, phoneIN, SHA1(passwordIN),0,0)$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `userUpdate` (IN `userIdIN` INT(8), IN `userIN` VARCHAR(100), IN `passwordIN` VARCHAR(100), IN `emailIN` VARCHAR(100), IN `isadminIN` BOOLEAN, IN `isdeletedIN` BOOLEAN)   UPDATE `users`
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
(52, 'Álló Antik Tükör', 'Nagyméretű álló antik tükör.', '150', 3, 17, '2024-12-05 14:00:42'),
(53, 'Vintage Seiko férfi karóra', '1970-es évekbeli japán Seiko karóra, rozsdamentes acél tokkal.', '45000', 4, 1, '2025-03-26 13:20:35'),
(54, 'Omega De Ville klasszikus óra', 'Elegáns, kézzel felhúzható mechanikus Omega óra bőrszíjjal.', '125000', 2, 1, '2025-03-26 13:20:35'),
(55, 'Zsebóra lánccal - antik stílus', 'Mechanikus zsebóra díszes gravírozással, lánccal együtt.', '28500', 6, 1, '2025-03-26 13:20:35'),
(56, 'Cartier Tank replika', 'Vintage megjelenésű Cartier Tank stílusú óra aranyszínű tokkal.', '39000', 5, 1, '2025-03-26 13:20:35'),
(57, 'Szovjet Raketa karóra', 'Retro orosz Raketa karóra piros számlappal, gyűjtőknek ajánlott.', '32000', 3, 1, '2025-03-26 13:20:35'),
(58, 'Antik falióra faragott fával', 'Nagy méretű, faragott fából készült falióra ingával.', '65000', 2, 1, '2025-03-26 13:20:35'),
(59, 'Casio digitális óra - 1980', 'Eredeti Casio digitális óra az 1980-as évekből.', '18000', 10, 1, '2025-03-26 13:20:35'),
(60, 'Fali óra római számokkal', 'Vintage stílusú falióra római számos számlappal.', '23000', 7, 1, '2025-03-26 13:20:35'),
(61, 'Junghans mechanikus óra', 'Német gyártású klasszikus Junghans óra automata szerkezettel.', '79000', 3, 1, '2025-03-26 13:20:35'),
(62, 'Swatch 1995 limitált kiadás', 'Színes Swatch karóra 1995-ből, gyűjtői darab.', '27000', 8, 1, '2025-03-26 13:20:35'),
(63, 'Aranyozott vintage nyaklánc', 'Elegáns, aranyozott lánc klasszikus medállal.', '42000', 5, 2, '2025-03-26 13:21:07'),
(64, 'Ezüst karkötő - 925 sterling', 'Finom kidolgozású ezüst karkötő, valódi sterling jelzéssel.', '37000', 6, 2, '2025-03-26 13:21:07'),
(65, 'Gyöngy fülbevaló', 'Klasszikus, kézzel készített gyöngy fülbevaló.', '25000', 8, 2, '2025-03-26 13:21:07'),
(66, 'Vintage bross - virágos motívum', 'Régi stílusú bross színes kövekkel és virágdísszel.', '18000', 10, 2, '2025-03-26 13:21:07'),
(67, 'Zománcos art deco nyaklánc', '1930-as évek stílusában zománcos medállal.', '34000', 4, 2, '2025-03-26 13:21:07'),
(68, 'Platina eljegyzési gyűrű', 'Prémium platina gyűrű gyémánt utánzattal.', '75000', 2, 2, '2025-03-26 13:21:07'),
(69, 'Antik ezüst medál', 'Különleges gravírozott ezüst medál, vésett mintával.', '29500', 5, 2, '2025-03-26 13:21:07'),
(70, 'Színes kristályos nyaklánc', 'Többsoros lánc csillogó kristály díszekkel.', '31000', 6, 2, '2025-03-26 13:21:07'),
(71, 'Retro gyűrű - türkiz kővel', 'Nagy méretű, feltűnő gyűrű türkiz kő díszítéssel.', '27000', 7, 2, '2025-03-26 13:21:07'),
(72, 'Szív alakú medál nyaklánccal', 'Érzelmes vintage medál, díszdobozos kiszerelésben.', '22000', 9, 2, '2025-03-26 13:21:07'),
(73, 'Antik bronz gyertyatartó', 'Masszív, kézzel faragott bronz gyertyatartó a XIX. századból.', '58000', 3, 3, '2025-03-26 13:21:17'),
(74, 'Kovácsoltvas falra szerelhető tartó', 'Díszes fali gyertyatartó rusztikus stílusban.', '31000', 5, 3, '2025-03-26 13:21:17'),
(75, 'Kristály gyertyatartó pár', 'Két darabos elegáns kristály szett ünnepi asztalra.', '47000', 4, 3, '2025-03-26 13:21:17'),
(76, 'Barokk stílusú gyertyatartó', 'Gazdagon díszített, aranyszínű antik hatású darab.', '39500', 6, 3, '2025-03-26 13:21:17'),
(77, 'Skandináv vintage fa tartó', 'Minimalista fa gyertyatartó északi stílusban.', '21500', 7, 3, '2025-03-26 13:21:17'),
(78, 'Üveg gyertyatartó 1930-ból', 'Art deco stílusú metszett üveg tartó, gyűjtői darab.', '36000', 4, 3, '2025-03-26 13:21:17'),
(79, 'Viktoriánus stílusú gyertyatartó', 'Finoman kidolgozott réz darab, angol stílusban.', '44000', 3, 3, '2025-03-26 13:21:17'),
(80, 'Porcelán virágos gyertyatartó', 'Festett porcelán gyertyatartó virágdíszítéssel.', '29500', 6, 3, '2025-03-26 13:21:17'),
(81, 'Retro háromágú gyertyatartó', 'Fekete fém test, klasszikus 60-as évekbeli formavilág.', '33000', 5, 3, '2025-03-26 13:21:17'),
(82, 'Álló kandalló gyertyatartó', 'Nagy méretű, elegáns álló gyertyatartó kandalló elé.', '67000', 2, 3, '2025-03-26 13:21:17'),
(83, 'Vintage bőr válltáska', 'Kézzel varrott, valódi bőrből készült klasszikus válltáska.', '52000', 4, 4, '2025-03-26 13:21:28'),
(84, 'Retro kézitáska', '1950-es évekbeli elegáns kézitáska fém csattal.', '39000', 6, 4, '2025-03-26 13:21:28'),
(85, 'Textil városi hátizsák', 'Vintage mintás, vízlepergető textil hátizsák.', '27500', 5, 4, '2025-03-26 13:21:28'),
(86, 'Patchwork oldaltáska', 'Kézzel készült színes patchwork mintás vászontáska.', '18000', 8, 4, '2025-03-26 13:21:28'),
(87, 'Klasszikus aktatáska', 'Fekete vintage aktatáska fémzárral és bőrből.', '45000', 3, 4, '2025-03-26 13:21:28'),
(88, 'Mini bőrtáska - kézzel festett', 'Kézzel festett virágmintás kisméretű vállpántos táska.', '32000', 4, 4, '2025-03-26 13:21:28'),
(89, 'Bélelt válltáska - kockás mintával', 'Puhán párnázott retró stílusú táska 1970-ből.', '26000', 7, 4, '2025-03-26 13:21:28'),
(90, 'Tengerészkék vászon oldaltáska', 'Időtálló vintage táska sárgaréz csatokkal.', '24000', 6, 4, '2025-03-26 13:21:28'),
(91, 'Retro ridikül - gyöngyház gombbal', 'Kézzel készített klasszikus női táska.', '29500', 5, 4, '2025-03-26 13:21:28'),
(92, 'Bőrből készült hátitáska', 'Eredeti retro túratáska valódi bőrből.', '48000', 3, 4, '2025-03-26 13:21:28'),
(93, 'Audio-Technica AT-LP120X', 'Professzionális bakelit lejátszó, precíz tűkar és USB digitalizálás.', '110000', 6, 5, '2025-03-26 13:21:39'),
(94, 'Sony PS-LX310BT', 'Bluetooth kompatibilis, automatikus működésű lemezjátszó.', '85000', 8, 5, '2025-03-26 13:21:39'),
(95, 'Technics SL-1210MK7', 'Legendás DJ lemezjátszó, masszív kivitel és kiváló hangminőség.', '160000', 4, 5, '2025-03-26 13:21:39'),
(96, 'Pro-Ject Debut Carbon Evo', 'Karbon karos prémium lejátszó csendes szíjhajtással.', '130000', 5, 5, '2025-03-26 13:21:39'),
(97, 'Rega Planar 3', 'Audiophile kategóriás kézi hajtású lejátszó.', '140000', 4, 5, '2025-03-26 13:21:39'),
(98, 'Victrola 8-in-1', 'Többfunkciós vintage lejátszó: CD, rádió, USB, kazetta.', '75000', 7, 5, '2025-03-26 13:21:39'),
(99, 'Denon DP-300F', 'Automata működés, kiváló hangzás, elegáns dizájn.', '90000', 9, 5, '2025-03-26 13:21:39'),
(100, 'Crosley Cruiser Deluxe', 'Hordozható vintage stílusú lejátszó, beépített hangszóróval.', '45000', 10, 5, '2025-03-26 13:21:39'),
(101, 'Lenco L-85', 'USB digitalizálás, beépített előerősítő, retró stílus.', '62000', 8, 5, '2025-03-26 13:21:39'),
(102, 'Fluance RT81', 'Masszív fa ház, prémium alkatrészek, kiváló hangzás.', '98000', 5, 5, '2025-03-26 13:21:39'),
(103, 'The Beatles - Abbey Road', 'Legendás rockalbum 1969-ből, eredeti bakelit kiadás.', '12500', 15, 6, '2025-03-26 13:21:51'),
(104, 'Pink Floyd - The Dark Side of the Moon', 'Klasszikus progresszív rock album különleges borítóval.', '13500', 10, 6, '2025-03-26 13:21:51'),
(105, 'Led Zeppelin IV', 'Az egyik legmeghatározóbb rocklemez, 1971.', '12800', 12, 6, '2025-03-26 13:21:51'),
(106, 'Queen - A Night at the Opera', 'Freddie Mercury ikonikus albuma, remek hangminőséggel.', '14000', 8, 6, '2025-03-26 13:21:51'),
(107, 'David Bowie - Ziggy Stardust', 'Stílusteremtő glam rock album Bowie-tól.', '13200', 9, 6, '2025-03-26 13:21:51'),
(108, 'Michael Jackson - Thriller', 'A világ legsikeresebb albuma, eredeti nyomat.', '14500', 7, 6, '2025-03-26 13:21:51'),
(109, 'Nirvana - Nevermind', 'Alternatív rock klasszikus, kultikus borítóval.', '12000', 14, 6, '2025-03-26 13:21:51'),
(110, 'The Rolling Stones - Sticky Fingers', 'Rock and roll klasszikus Andy Warhol borítóval.', '12500', 10, 6, '2025-03-26 13:21:51'),
(111, 'Metallica - Master of Puppets', 'Thrash metal mestermű, 1986-os kiadás.', '13000', 11, 6, '2025-03-26 13:21:51'),
(112, 'Radiohead - OK Computer', 'Alternatív rock alapmű 1997-ből.', '12700', 13, 6, '2025-03-26 13:21:51'),
(113, 'Modern Állólámpa', 'Minimalista stílusú LED állólámpa szövet burával.', '45000', 10, 7, '2025-03-26 13:21:59'),
(114, 'Asztali Olvasólámpa', 'Állítható karú lámpa meleg fényű LED izzóval.', '22000', 15, 7, '2025-03-26 13:21:59'),
(115, 'Kristály Csillár', 'Többszintes kristálycsillár klasszikus eleganciával.', '150000', 5, 7, '2025-03-26 13:21:59'),
(116, 'LED Mennyezeti Lámpa', 'Távirányítós fényerőszabályzással és színhőmérséklet-állítással.', '60000', 12, 7, '2025-03-26 13:21:59'),
(117, 'Indusztriális Fali Lámpa', 'Loft stílusú fém lámpa Edison izzóval.', '38000', 8, 7, '2025-03-26 13:21:59'),
(118, 'Napenergia Lámpa', 'Kültéri napelemes lámpa mozgásérzékelővel.', '25000', 20, 7, '2025-03-26 13:21:59'),
(119, 'Vintage Asztali Lámpa', 'Réz kivitelű, patinás hangulatvilágítás.', '42000', 6, 7, '2025-03-26 13:21:59'),
(120, 'Éjszakai Fény Projektor', 'Csillagos égbolt mintázatú projektoros lámpa.', '33000', 14, 7, '2025-03-26 13:21:59'),
(121, 'RGB Gamer Lámpa', 'Színes LED világítás, USB-s tápellátás, gamer dizájn.', '48000', 10, 7, '2025-03-26 13:21:59'),
(122, 'Japán Stílusú Papírlámpa', 'Természetes hatású papírbúra fa vázzal.', '27000', 18, 7, '2025-03-26 13:21:59'),
(123, 'Vintage Fém Falidísz', 'Kovácsoltvasból készült, ornamentikus mintázatú falidísz.', '32000', 6, 8, '2025-03-26 13:22:07'),
(124, 'Fa Keretes Fali Tükör', 'Kézzel faragott fa kerettel ellátott vintage tükör.', '46000', 4, 8, '2025-03-26 13:22:07'),
(125, 'Macskás Textil Falikép', 'Puha textilből készült falikép aranyos mintával.', '17500', 10, 8, '2025-03-26 13:22:07'),
(126, 'Kézzel Festett Falilap', 'Kerámia alapú, kézzel festett mediterrán stílusú dísz.', '29500', 5, 8, '2025-03-26 13:22:07'),
(127, 'Bordás Fa Dekorpanel', 'Skandináv stílusú fa dekorációs elem fali rögzítéssel.', '28000', 7, 8, '2025-03-26 13:22:07'),
(128, 'Retro Neon Fény Dekor', 'Rózsaszín neon világítású dekor felirat falra.', '34000', 3, 8, '2025-03-26 13:22:07'),
(129, 'Antik Réz Falitányér', 'Dombornyomott, falra akasztható réz dísztányér.', '23000', 6, 8, '2025-03-26 13:22:07'),
(130, 'Papírvirágos Falidísz', 'Színes papírvirágokból készült kézműves fali dekoráció.', '15000', 12, 8, '2025-03-26 13:22:07'),
(131, 'Buddha Fali Dombormű', 'Fából faragott spirituális témájú dombormű.', '39000', 4, 8, '2025-03-26 13:22:07'),
(132, 'Falra Szerelhető Polcdekor', 'Minimalista lebegő polc mintás háttérrel.', '25000', 8, 8, '2025-03-26 13:22:07'),
(133, 'Bögre - Macskás design', 'Kerámia bögre macskás mintával, kézzel festett.', '4500', 30, 9, '2025-03-26 13:22:21'),
(134, 'Vintage Porcelán Bögre', 'Klasszikus mintázatú, prémium porcelánból készült bögre.', '8900', 12, 9, '2025-03-26 13:22:21'),
(135, 'Termo Bögre', 'Hőtartó, fedeles bögre rozsdamentes acél belsővel.', '6500', 15, 9, '2025-03-26 13:22:21'),
(136, 'Vicces Feliratos Bögre', 'Szórakoztató szöveggel ellátott ajándékbögre.', '3500', 50, 9, '2025-03-26 13:22:21'),
(137, 'Színes Zománcozott Bögre', 'Retro stílusú, könnyű zománcozott fém bögre.', '5700', 20, 9, '2025-03-26 13:22:21'),
(138, 'Nagyméretű Kávés Bögre', 'Extra nagy kapacitású bögre kényelmes fogantyúval.', '7500', 18, 9, '2025-03-26 13:22:21'),
(139, 'Elegáns Üveg Bögre', 'Minimalista design, hőálló üvegből készült.', '9900', 10, 9, '2025-03-26 13:22:21'),
(140, 'Fekete Matt Bögre', 'Modern stílusú, strapabíró kerámiabögre matt bevonattal.', '5200', 22, 9, '2025-03-26 13:22:21'),
(141, 'Kerámia Latte Bögre', 'Hosszúkás forma, barista stílusú latte bögre.', '6800', 25, 9, '2025-03-26 13:22:21'),
(142, 'Művészi Mintás Bögre', 'Egyedi festésű, kézzel készített művészi bögre.', '7200', 14, 9, '2025-03-26 13:22:21'),
(143, 'Polaroid Land Camera 1000', 'Azonnali képnyomtatós retro fényképezőgép, szivárvány csíkkal.', '39000', 5, 10, '2025-03-26 13:22:31'),
(144, 'Zenit-E Szovjet Kamera', 'Klasszikus filmes tükörreflexes fényképezőgép Helios objektívvel.', '45000', 3, 10, '2025-03-26 13:22:31'),
(145, 'Canon AE-1 Program', '1980-as évekbeli filmes SLR, kiváló kezdőknek.', '62000', 4, 10, '2025-03-26 13:22:31'),
(146, 'Kodak Brownie Flash II', 'Vintage boxkamera, gyűjtői darab.', '28000', 6, 10, '2025-03-26 13:22:31'),
(147, 'Olympus Trip 35', 'Kompakt filmes kamera, letisztult dizájn, fix objektívvel.', '51000', 5, 10, '2025-03-26 13:22:31'),
(148, 'Minolta X-700', 'SLR gép automatikus és manuális móddal, filmes fotózáshoz.', '58000', 4, 10, '2025-03-26 13:22:31'),
(149, 'Agfa Clack', 'Német boxkamera 1950-es évekből, középformátumú filmre.', '31000', 3, 10, '2025-03-26 13:22:31'),
(150, 'Leica M3 replika', 'Klasszikus Leica stílusú filmes gép, makulátlan állapotban.', '87000', 2, 10, '2025-03-26 13:22:31'),
(151, 'Yashica Mat-124 G', 'Kétobjektíves tükörreflexes középformátumú fényképezőgép.', '96000', 2, 10, '2025-03-26 13:22:31'),
(152, 'Fujica ST605N', 'Kézi filmes SLR fémházas kivitelben, M42 menetes objektívvel.', '43000', 4, 10, '2025-03-26 13:22:31'),
(153, 'Nintendo Entertainment System (NES)', 'Az 1980-as évek ikonikus konzolja, eredeti kontrollerrel.', '55000', 4, 11, '2025-03-26 13:22:42'),
(154, 'Super Nintendo (SNES)', 'A 16 bites korszak klasszikusa, számos legendás játékkal.', '60000', 5, 11, '2025-03-26 13:22:42'),
(155, 'Sega Mega Drive II', 'A SEGA 16 bites konzolja, Sonic játékokkal ismert.', '48000', 6, 11, '2025-03-26 13:22:42'),
(156, 'Sony PlayStation 1 (PS1)', 'Az első PlayStation, CD-s játékokkal, 1994-ből.', '45000', 8, 11, '2025-03-26 13:22:42'),
(157, 'Nintendo 64', 'Első 3D-s Nintendo konzol, klasszikus kontroller dizájnnal.', '59000', 5, 11, '2025-03-26 13:22:42'),
(158, 'Game Boy Classic', 'Hordozható, monokróm kijelzős konzol, Tetris-szel ismert.', '35000', 10, 11, '2025-03-26 13:22:42'),
(159, 'Atari 2600', 'A videojáték-ipar egyik első otthoni konzolja.', '40000', 3, 11, '2025-03-26 13:22:42'),
(160, 'Sega Dreamcast', 'SEGA utolsó konzolja, ahead of its time technológiával.', '62000', 4, 11, '2025-03-26 13:22:42'),
(161, 'Neo Geo AES', 'Gyűjtői ritkaság, arcade minőség otthoni konzolban.', '125000', 2, 11, '2025-03-26 13:22:42'),
(162, 'PlayStation 2', 'A valaha volt legnagyobb példányszámban eladott konzol.', '38000', 12, 11, '2025-03-26 13:22:42'),
(163, 'LEGO Space Classic 928', 'Az 1979-es klasszikus LEGO űrhajó, eredeti alkatrészekkel.', '89000', 2, 12, '2025-03-26 13:22:52'),
(164, 'Rubik Kocka - eredeti 1980-as kiadás', 'Magyar találmány, a világ egyik legismertebb logikai játéka.', '12000', 15, 12, '2025-03-26 13:22:52'),
(165, 'Matchbox kisautó - 1975', 'Fém kisautó gyűjtőknek, eredeti csomagolás nélkül.', '8500', 20, 12, '2025-03-26 13:22:52'),
(166, 'Playmobil Lovagvár (1984)', 'Teljes készlet retro lovag figurákkal és kiegészítőkkel.', '47000', 4, 12, '2025-03-26 13:22:52'),
(167, 'Monchhichi plüssfigura', '1980-as évek japán eredetű gyűjtői játék.', '15000', 7, 12, '2025-03-26 13:22:52'),
(168, 'G.I. Joe figura - Cobra Commander', 'Eredeti 1986-os G.I. Joe figura mozgatható végtagokkal.', '22000', 5, 12, '2025-03-26 13:22:52'),
(169, 'Transformers G1 Megatron', '1984-es eredeti Hasbro Transformers figura.', '38000', 3, 12, '2025-03-26 13:22:52'),
(170, 'Nintendo Game & Watch – Donkey Kong', 'Kézi LCD játék a korai 1980-as évekből.', '42000', 2, 12, '2025-03-26 13:22:52'),
(171, 'Barbie - 1960-as Vintage kiadás', 'Klasszikus Barbie baba korhű ruhával, gyűjtői állapotban.', '65000', 2, 12, '2025-03-26 13:22:52'),
(172, 'Tamagotchi - eredeti Bandai', 'Digitális kisállat 1997-ből, működőképes állapotban.', '18000', 6, 12, '2025-03-26 13:22:52'),
(173, 'Kínai Porcelán Váza', 'Kézzel festett, tradicionális kék-fehér kínai porcelán váza.', '120000', 3, 13, '2025-03-26 13:23:01'),
(174, 'Herendi Teás Készlet', 'Exkluzív magyar porcelán szett virágmintával, 6 személyes.', '250000', 2, 13, '2025-03-26 13:23:01'),
(175, 'Japán Porcelán Tányér', 'Finoman kidolgozott tányér, kék-fehér hullámmintával.', '85000', 5, 13, '2025-03-26 13:23:01'),
(176, 'Vintage Porcelán Csésze', 'Antik kézzel festett csésze aranyszegéllyel.', '35000', 10, 13, '2025-03-26 13:23:01'),
(177, 'Porcelán Ékszerdoboz', 'Dekoratív, kisméretű dobozka virágmintás porcelánból.', '65000', 8, 13, '2025-03-26 13:23:01'),
(178, 'Barokk Stílusú Porcelán Gyertyatartó', 'Díszes, aranyozott részletekkel gazdagított gyertyatartó.', '95000', 4, 13, '2025-03-26 13:23:01'),
(179, 'Antik Francia Porcelán Tál', 'Kézi festés és aranyozott perem jellemzi ezt a klasszikus tálat.', '130000', 3, 13, '2025-03-26 13:23:01'),
(180, 'Porcelán Fali Dekoráció', 'Faliképként is használható porcelán dísztárgy.', '78000', 6, 13, '2025-03-26 13:23:01'),
(181, 'Kézzel Festett Porcelán Tálca', 'Egyedi festésű tálca, klasszikus európai motívumokkal.', '112000', 5, 13, '2025-03-26 13:23:01'),
(182, 'Modern Minimalista Porcelán Edénykészlet', 'Letisztult dizájn, matt fehér bevonattal, 4 részes.', '145000', 7, 13, '2025-03-26 13:23:01'),
(183, 'Vintage Bőrönd - Barna Bőr', 'Kézzel varrott valódi bőrönd réz csatokkal, 1950-es évek.', '78000', 2, 14, '2025-03-26 13:23:10'),
(184, 'Retro Textil Bőrönd', 'Klasszikus kockás mintázatú utazótáska merevítéssel.', '42000', 4, 14, '2025-03-26 13:23:10'),
(185, 'Régi Fém Koffer', 'Ipari stílusú, strapabíró fém koffer fogantyúval.', '58000', 3, 14, '2025-03-26 13:23:10'),
(186, 'Vintage Utazóláda', 'Fából készült nagyméretű láda utazásokhoz, rézveretekkel.', '96000', 1, 14, '2025-03-26 13:23:10'),
(187, 'Kézipoggyász - 1960-as évek', 'Retro mintás kézitáska műbőrből, korhű állapotban.', '29000', 5, 14, '2025-03-26 13:23:10'),
(188, 'Antik Vasúti Bőrönd', 'Vasúti utazásokra használt keményfedeles táska.', '61000', 2, 14, '2025-03-26 13:23:10'),
(189, 'Steamer Trunk - Régi Gőzhajós Láda', 'Gyűjtői darab klasszikus bőr utazóláda.', '120000', 1, 14, '2025-03-26 13:23:10'),
(190, 'Retro Sporttáska Adidas (1970)', 'Eredeti Adidas sporttáska klasszikus logóval.', '37000', 3, 14, '2025-03-26 13:23:10'),
(191, 'Bőrönd Szett - háromrészes', 'Vintage megjelenésű puha borítású bőrönd szett.', '88000', 2, 14, '2025-03-26 13:23:10'),
(192, 'Hordozható Gramofon Tok', 'Régi gramofon tárolására szolgáló hordozható tok.', '45000', 2, 14, '2025-03-26 13:23:10'),
(193, 'Ezüst Evőeszköz Készlet (12 személyes)', '925-ös ezüstből készült klasszikus evőeszköz szett díszdobozban.', '185000', 1, 15, '2025-03-26 13:23:18'),
(194, 'Antik Francia Villa és Kés Páros', '1900-as évekbeli francia evőeszközök gravírozott nyéllel.', '42000', 3, 15, '2025-03-26 13:23:18'),
(195, 'Angol Teáskanál Szett (6 db)', 'Szecessziós stílusú teáskanalak ezüstözött kivitelben.', '29000', 4, 15, '2025-03-26 13:23:18'),
(196, 'Retro Piknik Evőeszközkészlet', 'Műanyag és fém evőeszközök színes tokban, 1970-es évek.', '12000', 6, 15, '2025-03-26 13:23:18'),
(197, 'Kézzel Kalapált Réz Evőeszközök', 'Különleges, kézműves réz szett étkezésekhez.', '35000', 2, 15, '2025-03-26 13:23:18'),
(198, 'Art Deco Stílusú Késkészlet', 'Geometrikus mintázatú, acél pengés vintage kések.', '27000', 5, 15, '2025-03-26 13:23:18'),
(199, 'Gyermek Evőeszköz Szett - 1960', 'Gravírozott állatfigurás kis méretű készlet.', '15000', 7, 15, '2025-03-26 13:23:18'),
(200, 'Ezüstözött Tálalókanalak (3 db)', 'Nagyméretű, díszített evőeszközök tálaláshoz.', '24000', 3, 15, '2025-03-26 13:23:18'),
(201, 'Porcelán Nyelű Evőeszközök', 'Különleges, festett nyelű evőeszköz szett.', '31000', 4, 15, '2025-03-26 13:23:18'),
(202, 'Bakelit Nyelű Retro Evőeszközök', '1950-es évekbeli evőeszközök színes bakelit nyéllel.', '18000', 6, 15, '2025-03-26 13:23:18'),
(203, 'Antik Álló Ruhafogas', 'Tömörfa álló fogas, hajlított akasztókarokkal, 1920-as évekből.', '64000', 2, 16, '2025-03-26 13:23:26'),
(204, 'Kovácsoltvas Fali Fogas', 'Kézzel kovácsolt vas fali akasztó 5 kampóval.', '28000', 4, 16, '2025-03-26 13:23:26'),
(205, 'Vintage Falifogas Tükörrel', 'Tükörrel kombinált, díszes fából készült fogas.', '39000', 3, 16, '2025-03-26 13:23:26'),
(206, 'Retró Műanyag Fogas Szett (6 db)', 'Színes műanyag ruhafogasok 1970-es évekből.', '9500', 8, 16, '2025-03-26 13:23:26'),
(207, 'Art Deco Stílusú Ruhafogas', 'Króm és fa kombináció, elegáns geometrikus formákkal.', '52000', 2, 16, '2025-03-26 13:23:26'),
(208, 'Térelválasztós Fogas Panel', 'Nagy méretű falpanel akasztókkal és kalaptartóval.', '87000', 1, 16, '2025-03-26 13:23:26'),
(209, 'Fali Fogas Színes Kerámiagombbal', 'Rusztikus fa alap, festett kerámia akasztófejekkel.', '33000', 5, 16, '2025-03-26 13:23:26'),
(210, 'Bakelit Akasztós Fogas', '1950-es évekből származó fali fogas retró bakelit panelekből.', '24500', 3, 16, '2025-03-26 13:23:26'),
(211, 'Kihajtható Ruhafogas', 'Falra szerelhető, fából készült, kihajtható szerkezet.', '19500', 6, 16, '2025-03-26 13:23:26'),
(212, 'Gyermek Ruhafogas – Állatos Mintával', 'Kézzel festett fa fogas gyerekeknek.', '16000', 7, 16, '2025-03-26 13:23:26'),
(213, 'Kovácsoltvas Keretes Fali Tükör', 'Kézzel kovácsolt, díszes mintázatú vas keret, rusztikus stílus.', '48000', 3, 17, '2025-03-26 13:23:34'),
(214, 'Barokk Stílusú Aranyozott Tükör', 'Részletgazdag, faragott fa keret aranyozott bevonattal.', '88000', 2, 17, '2025-03-26 13:23:34'),
(215, 'Ovális Asztali Sminktükör', 'Rézállványos, dönthető ovális tükör, 1920-as évekből.', '32000', 4, 17, '2025-03-26 13:23:34'),
(216, 'Vintage Fali Tükör Faragott Keretben', 'Díszes, kézzel faragott keret, patinás felület.', '55000', 3, 17, '2025-03-26 13:23:34'),
(217, 'Retró Kerek Tükör Fa Kerettel', '1960-as évek skandináv stílusú minimalista fa keret.', '37000', 5, 17, '2025-03-26 13:23:34'),
(218, 'Állótükör – Viktoriánus stílus', 'Nagy méretű, díszített lábas tükör, klasszikus hálószobába.', '94000', 1, 17, '2025-03-26 13:23:34'),
(219, 'Kézitükör – Ezüst Nyéllel', 'Antik kézitükör vésett ezüst nyéllel és elegáns kialakítással.', '29000', 6, 17, '2025-03-26 13:23:34'),
(220, 'Keret nélküli Fali Tükör (1950)', 'Egyszerű, fazettás üvegperemmel ellátott vintage darab.', '22000', 4, 17, '2025-03-26 13:23:34'),
(221, 'Napmotívumos Dekortükör', 'Dekoratív tükör fém sugármintás kerettel.', '41000', 3, 17, '2025-03-26 13:23:34'),
(222, 'Antik Szalon Tükör', 'Nagy, ovális álló tükör hajlított fa lábazattal.', '79000', 2, 17, '2025-03-26 13:23:34'),
(223, 'Antik Fa Könyvtámasz Pár', 'Kézzel faragott sötét diófából készült könyvtámaszok.', '36000', 3, 18, '2025-03-26 13:23:43'),
(224, 'Vintage Kovácsoltvas Könyvtartó', 'Kézzel kovácsolt fém állvány, díszített mintával.', '32000', 4, 18, '2025-03-26 13:23:43'),
(225, 'Art Deco Könyvállvány', 'Geometrikus formák, fa és fém kombináció, 1930-as évekből.', '47000', 2, 18, '2025-03-26 13:23:43'),
(226, 'Fa Asztali Könyvolvasó Állvány', 'Dönthető asztali állvány könyvek vagy receptek tartásához.', '22000', 6, 18, '2025-03-26 13:23:43'),
(227, 'Retro Plexi Könyvtartó', '1970-es évekbeli átlátszó plexiből készült darab.', '18000', 5, 18, '2025-03-26 13:23:43'),
(228, 'Kézzel Festett Könyvtámasz', 'Színes virágmintával díszített kézműves fa könyvtámasz pár.', '26000', 4, 18, '2025-03-26 13:23:43'),
(229, 'Bakelit Lemezformájú Könyvtartó', 'Kreatív könyvtámasz bakelit lemezek ihlette dizájnnal.', '24000', 3, 18, '2025-03-26 13:23:43'),
(230, 'Minimalista Fém Könyvállvány', 'Egyszerű vonalvezetésű fekete fémállvány.', '20000', 7, 18, '2025-03-26 13:23:43'),
(231, 'Vintage Üvegpolcos Könyvespolc', 'Kis méretű, üvegpolcos fali könyvtartó.', '39000', 2, 18, '2025-03-26 13:23:43'),
(232, 'Vasúti Sín Darabból Készült Könyvtámasz', 'Egyedi ipari stílusú, nehéz fémtömb könyvtámasz.', '50000', 1, 18, '2025-03-26 13:23:43');

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
-- Table structure for table `user`
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
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `username`, `firstname`, `lastname`, `email`, `phone_number`, `password`, `is_admin`, `is_deleted`, `created_at`, `deleted_at`, `profile_picture`) VALUES
(1, 'hermannmate420', 'Mate', 'Hermann', 'hermate67@gmail.com', '+36074088704', '58b080f4d0240741b9ef583fc1106e9bcabf2042', 1, 0, '2025-02-12 01:01:40', NULL, 'C:\\wildfly\\standalone\\data/uploads\\profile_1_1742945955279.png'),
(2, 'Matevagyok', 'Énvagyok', 'Mate', 'hddgamer88@gmail.com', '+367848484848', '664819d8c5343676c9225b5ed00a5cdc6f3a1ff3', 0, 0, '2025-02-25 12:00:00', NULL, 'C:\\wildfly\\standalone\\data\\uploads\\profile_2_1742981105416.png'),
(3, 'NotAdmin', 'Gábor', 'Attila', 'attila.gabor@gmail.com', '+3456642257', '510bf3284d7f0f3cb996c2e0163d7e86aee40a69', 0, 0, '2025-03-04 18:01:59', NULL, NULL),
(4, 'Tesztelek54', 'Teszt', 'Elek', 'tesztelek@gmail.com', '+3670585895', '803de60414f888d695aadd09de69a78f8b470547', 1, 0, '2025-03-07 11:14:02', NULL, NULL),
(5, 'TesztAdmin', 'Admin', 'Teszt', 'AdminTeszt@gmail.com', '+3673777373', '5c8956161238b0a10da03c7bfe4f152e15bb1d70', 1, 0, '2025-03-11 10:52:56', NULL, NULL),
(6, 'AdminTeszt', 'Teszt', 'Admin', 'TesztAdmin@gmail.com', '+3673754543', 'd5198e4889a9365dc467262fdef7643158b05a9a', 1, 0, '2025-03-11 11:50:54', NULL, NULL);

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
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

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
  MODIFY `product_id` int(8) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=233;

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
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
