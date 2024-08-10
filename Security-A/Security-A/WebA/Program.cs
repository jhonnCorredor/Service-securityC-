using Business.Implements.Operational;
using Business.Implements.Parameter;
using Business.Implements.Security;
using Business.Interfaces.Operational;
using Business.Interfaces.Parameter;
using Business.Interfaces.Security;
using Data.Implements.Operational;
using Data.Implements.Parameter;
using Data.Implements.Security;
using Data.Interfaces.Operational;
using Data.Interfaces.Parameter;
using Data.Interfaces.Security;
using Entity.Context;
using Microsoft.EntityFrameworkCore;

namespace WebA
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            //Configuracion Cords
            builder.Services.AddCors(options =>
            {
                options.AddPolicy("AllowSpecificOrigin",
                    builder =>
                    {
                        builder.WithOrigins("http://localhost:4200")
                               .AllowAnyHeader()
                               .AllowAnyMethod();
                    });
            });

            // Configura DbContext con SQL Server
            builder.Services.AddDbContext<ApplicationDBContext>(options =>
                options.UseSqlServer(builder.Configuration.GetConnectionString("DbfaultConnection")));

            //Configuiracion de Data I,S
            builder.Services.AddScoped<IModuloData, ModuloData>();
            builder.Services.AddScoped<IPersonData, PersonData>();
            builder.Services.AddScoped<IRoleData, RoleData>();
            builder.Services.AddScoped<IRoleViewData, RoleViewData>();
            builder.Services.AddScoped<IUserData, UserData>();
            builder.Services.AddScoped<IUserRoleData, UserRoleData>();
            builder.Services.AddScoped<IViewData, ViewData>();
            builder.Services.AddScoped<IContinentData, ContinentData>();
            builder.Services.AddScoped<ICountryData, CountryData>();
            builder.Services.AddScoped<ICityData, CityData>();
            builder.Services.AddScoped<IFarmData, FarmData>();
            builder.Services.AddScoped<IAssesmentCriteriaData, AssesmentCriteriaData>();
            builder.Services.AddScoped<IChecklistData, ChecklistData>();
            builder.Services.AddScoped<ICropData, CropData>();
            builder.Services.AddScoped<IEvidenceData, EvidenceData>();
            builder.Services.AddScoped<IFarmCropData, FarmCropData>();
            builder.Services.AddScoped<IFertilizationData, FertilizationData>();
            builder.Services.AddScoped<IFertilizationSuppliesData, FertilizationSuppliesData>();
            builder.Services.AddScoped<IFumigationData, FumigationData>();
            builder.Services.AddScoped<IFumigationSuppliesData, FumigationSuppliesData>();
            builder.Services.AddScoped<IQualificationData, QualificationData>();
            builder.Services.AddScoped<IReviewEvidenceData, ReviewEvidenceData>();
            builder.Services.AddScoped<IReviewTechnicalData, ReviewTechnicalData>();
            builder.Services.AddScoped<ISuppliesData, SuppliesData>();

            //Configuracion de Business I,S
            builder.Services.AddScoped<IModuloBusiness, ModuloBusiness>();
            builder.Services.AddScoped<IPersonBusiness, PersonBusiness>();
            builder.Services.AddScoped<IRoleBusiness, RoleBusiness>();
            builder.Services.AddScoped<IRoleViewBusiness, RoleViewBusiness>();
            builder.Services.AddScoped<IUserBusiness, UserBusiness>();
            builder.Services.AddScoped<IUserRoleBusiness, UserRoleBusiness>();
            builder.Services.AddScoped<IViewBusiness, ViewBusiness>();
            builder.Services.AddScoped<IContinentBusiness, ContinentBusiness>();
            builder.Services.AddScoped<ICountryBusiness, CountryBusiness>();
            builder.Services.AddScoped<ICityBusiness, CityBusiness>();
            builder.Services.AddScoped<IFarmBusiness, FarmBusiness>();
            builder.Services.AddScoped<IFarmCropBusiness, FarmCropBusiness>();
            builder.Services.AddScoped<IChecklistBusiness, ChecklistBusiness>();
            builder.Services.AddScoped<IEvidenceBusiness, EvidenceBusiness>();
            builder.Services.AddScoped<ICropBusiness, CropBusiness>();
            builder.Services.AddScoped<IAssesmentCriteriaBusiness, AssesmentCriteriaBusiness>();
            builder.Services.AddScoped<IFertilizationBusiness, FertilizationBusiness>();
            builder.Services.AddScoped<IFertilizationSuppliesBusiness, FertilizationSuppliesBusiness>();
            builder.Services.AddScoped<IFumigationBusiness, FumigationBusiness>();
            builder.Services.AddScoped<IFumigationSuppliesBusiness, FumigationSuppliesBusiness>();
            builder.Services.AddScoped<IQualificationBusiness, QualificationBusiness>();
            builder.Services.AddScoped<IReviewEvidenceBusiness, ReviewEvidenceBusiness>();
            builder.Services.AddScoped<IReviewTechnicalBusiness, ReviewTechnicalBusiness>();
            builder.Services.AddScoped<ISuppliesBusiness, SuppliesBusiness>();



            // Add services to the container.
            builder.Services.AddControllers();


            // Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
            builder.Services.AddEndpointsApiExplorer();
            builder.Services.AddSwaggerGen();

            var app = builder.Build();

            // Configure the HTTP request pipeline.
            if (app.Environment.IsDevelopment())
            {
                app.UseCors("AllowSpecificOrigin");
                app.UseSwagger();
                app.UseSwaggerUI();
            }



            app.UseHttpsRedirection();
            app.UseAuthentication();
            app.UseAuthorization();


            app.MapControllers();

            app.Run();
        }
    }
}