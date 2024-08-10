using Entity.Dto;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Entity.Dto.Parameter;
using Entity.Model.Parameter;

namespace Business.Interfaces.Parameter
{
    public interface IFarmCropBusiness
    {
        Task Delete(int id);
        Task<IEnumerable<DataSelectDto>> GetAllSelect();
        Task<FarmCropDto> GetById(int id);
        Task<FarmCrop> Save(FarmCropDto entity);
        Task Update(FarmCropDto entity);
        FarmCrop mapearDatos(FarmCrop farmCrop, FarmCropDto entity);
        Task<IEnumerable<FarmCropDto>> GetAll();
    }
}
