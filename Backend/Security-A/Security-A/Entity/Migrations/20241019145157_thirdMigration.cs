using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Entity.Migrations
{
    /// <inheritdoc />
    public partial class thirdMigration : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Persons_Citys_CityId",
                table: "Persons");

            migrationBuilder.AddColumn<byte[]>(
                name: "Photo",
                table: "Users",
                type: "varbinary(max)",
                nullable: true);

            migrationBuilder.AlterColumn<int>(
                name: "CityId",
                table: "Persons",
                type: "int",
                nullable: true,
                oldClrType: typeof(int),
                oldType: "int");

            migrationBuilder.AlterColumn<DateTime>(
                name: "Birth_of_date",
                table: "Persons",
                type: "datetime2",
                nullable: true,
                oldClrType: typeof(DateTime),
                oldType: "datetime2");

            migrationBuilder.AlterColumn<string>(
                name: "Addres",
                table: "Persons",
                type: "nvarchar(max)",
                nullable: true,
                oldClrType: typeof(string),
                oldType: "nvarchar(max)");

            migrationBuilder.AddColumn<byte[]>(
                name: "DocumentBinary",
                table: "Evidences",
                type: "varbinary(max)",
                nullable: true);

            migrationBuilder.Sql("UPDATE Evidences SET DocumentBinary = CONVERT(varbinary(max), Document)");

            migrationBuilder.DropColumn(
                name: "Document",
                table: "Evidences");

            migrationBuilder.RenameColumn(
                name: "DocumentBinary",
                table: "Evidences",
                newName: "Document");

            migrationBuilder.AddForeignKey(
                name: "FK_Persons_Citys_CityId",
                table: "Persons",
                column: "CityId",
                principalTable: "Citys",
                principalColumn: "Id");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Persons_Citys_CityId",
                table: "Persons");

            migrationBuilder.DropColumn(
                name: "Photo",
                table: "Users");

            migrationBuilder.AlterColumn<int>(
                name: "CityId",
                table: "Persons",
                type: "int",
                nullable: false,
                defaultValue: 0,
                oldClrType: typeof(int),
                oldType: "int",
                oldNullable: true);

            migrationBuilder.AlterColumn<DateTime>(
                name: "Birth_of_date",
                table: "Persons",
                type: "datetime2",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified),
                oldClrType: typeof(DateTime),
                oldType: "datetime2",
                oldNullable: true);

            migrationBuilder.AlterColumn<string>(
                name: "Addres",
                table: "Persons",
                type: "nvarchar(max)",
                nullable: false,
                defaultValue: "",
                oldClrType: typeof(string),
                oldType: "nvarchar(max)",
                oldNullable: true);

            migrationBuilder.AddColumn<string>(
                name: "Document",
                table: "Evidences",
                type: "nvarchar(max)",
                nullable: false);

            migrationBuilder.Sql("UPDATE Evidences SET Document = CONVERT(nvarchar(max), DocumentBinary)");

            migrationBuilder.DropColumn(
                name: "DocumentBinary",
                table: "Evidences");

            migrationBuilder.AddForeignKey(
                name: "FK_Persons_Citys_CityId",
                table: "Persons",
                column: "CityId",
                principalTable: "Citys",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

    }
}
